import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject
import java.io.File
import java.net.ConnectException
import java.net.SocketException
import java.net.URI
import java.util.concurrent.Executors

// customize as per needed
val baseScrapeCacheDir = File("D:/scrape-cache")
val virtualDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
val cpuDispatcher = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()

suspend fun <T, R> List<T>.executeTask(
    taskIdentifier: String,
    dispatcher: CoroutineDispatcher = cpuDispatcher,
    taskProcessor: suspend (T) -> R
): List<TaskResult<T, R>> = coroutineScope {
    val progressBarWidth = 50
    val tasks = this@executeTask

    val startTime = System.currentTimeMillis()
    val totalTasks = tasks.size
    val header = "Task: $taskIdentifier | Count: $totalTasks"

    val progressEvents = Channel<TaskResult<T, R>>(Channel.UNLIMITED)

    val progressJob = launch {
        var success = 0
        var error = 0

        for (event in progressEvents) {
            when (event) {
                is TaskResult.Success<T, R> -> success++
                is TaskResult.Failure<T, R> -> error++
            }

            val completed = success + error
            val percent = completed * 100 / totalTasks
            val completeWidth = progressBarWidth * percent / 100
            val bar = "[" + "=".repeat(completeWidth) +
                    " ".repeat(progressBarWidth - completeWidth) + "]"

            print(
                "\r$header | Progress: $bar $percent% " +
                        "| Success: $success | Errors: $error"
            )
        }
    }

    val results = withContext(dispatcher) {
        tasks.map { task ->
            async {
                getResult(task, taskProcessor).also {
                    progressEvents.send(it)
                }
            }
        }.awaitAll()
    }

    progressEvents.close()
    progressJob.join()

    val endTime = System.currentTimeMillis()

    val successful = results.count { it is TaskResult.Success }
    val failed = results.count { it is TaskResult.Failure }
    println(
        "\r$header" +
                " | Completed in ${(endTime - startTime) / 1000.0} seconds" +
                " | Results: \u001B[32m$successful successful\u001B[0m, \u001B[31m$failed failed\u001B[0m"
    )

    return@coroutineScope results
}

private suspend fun <T, R> getResult(
    task: T,
    taskProcessor: suspend (T) -> R,
    maxRetries: Int = 3
): TaskResult<T, R> {
    repeat(maxRetries) {
        try {
            return TaskResult.Success(task, taskProcessor(task))
        } catch (_: SocketException) {
            delay(50)
        } catch (_: ConnectException) {
            delay(50)
        } catch (e: Exception) {
            return TaskResult.Failure(task, e)
        }
    }
    return try {
        TaskResult.Success(task, taskProcessor(task))
    } catch (e: Exception) {
        TaskResult.Failure(task, e)
    }
}

suspend fun <R : SerializableObject> List<File>.executeTask(
    taskIdentifier: String,
    taskSerializer: suspend (JSONObject) -> R,
    taskProcessor: suspend (File) -> R?
): List<TaskResult<File, R?>> =
    executeTask(taskIdentifier) { file ->
        val cacheFile = File("${file}.cache")
        if (file.exists())
            taskProcessor(file).also { result ->
                cacheFile.writeText(result?.toObject()?.toPrettyString() ?: "null")
                file.delete()
            }
        else if (cacheFile.exists())
            cacheFile.toJSON()
                .takeIf { !it.isNull }
                ?.let { taskSerializer(it.toObject()) }
        else
            throw NoSuchFileException(file)
    }

suspend fun <T, R1, R2> List<TaskResult<T, R1>>.executeTask(
    taskIdentifier: String,
    taskProcessor: suspend (T, R1) -> R2
): List<TaskResult<T, R2>> =
    executeTask(taskIdentifier) {
        it.map { (task, result) -> taskProcessor(task, result) }
    }.mapResults()

suspend fun <T, R : SerializableObject> List<TaskResult<T, File>>.executeTask(
    taskIdentifier: String,
    taskSerializer: suspend (JSONObject) -> R,
    taskProcessor: suspend (T, File) -> R?
): List<TaskResult<T, R?>> =
    executeTask(taskIdentifier) { taskResult ->
        taskResult.map { (task, file) ->
            val cacheFile = File("${file}.cache")
            if (file.exists())
                taskProcessor(task, file).also { result ->
                    cacheFile.writeText(result?.toObject()?.toPrettyString() ?: "null")
                    file.delete()
                }
            else if (cacheFile.exists())
                cacheFile.toJSON()
                    .takeIf { !it.isNull }
                    ?.let { taskSerializer(it.toObject()) }
            else
                throw NoSuchFileException(file)
        }
    }.mapResults()

fun <T, R1, R2> List<TaskResult<TaskResult<T, R1>, R2>>.mapResults(): List<TaskResult<T, R2>> = map { result ->
    result.map(
        successMapper = { (task, result) -> TaskResult.Success(task.task, result) },
        failureMapper = { (task, error) -> TaskResult.Failure(task.task, error) }
    )
}

suspend fun <T> List<T>.executeScrapeTask(
    taskIdentifier: String,
    cachable: Boolean = false,
    dispatcher: CoroutineDispatcher = virtualDispatcher,
    urlFileProcessor: suspend (T) -> Pair<String, String>
): List<TaskResult<T, File>> =
    executeTask(taskIdentifier, dispatcher) { task ->
        urlFileProcessor(task).let { (url, fileName) ->
            File(baseScrapeCacheDir, fileName).also { file ->
                if (!file.exists() && !(cachable && File("${file}.cache").exists())) {
                    file.parentFile.mkdirs()
                    file.writeBytes(URI(url).toURL().readBytes())
                }
            }
        }
    }

suspend fun <T, R> List<TaskResult<T, R>>.executeScrapeTask(
    taskIdentifier: String,
    cachable: Boolean = false,
    dispatcher: CoroutineDispatcher = virtualDispatcher,
    urlFileProcessor: suspend (T, R) -> Pair<String, String>
): List<TaskResult<T, File>> =
    executeScrapeTask(taskIdentifier, cachable, dispatcher) {
        it.map { (task, result) -> urlFileProcessor(task, result) }
    }.mapResults()

fun <T, R> List<TaskResult<T, out Iterable<R>>>.flattenTaskResults(): List<TaskResult<T, R>> = flatMap { taskResult ->
    taskResult.map(
        successMapper = { success -> success.result.map { element -> success.mapResult { element } } },
        failureMapper = { listOf(it.mapToNewType()) }
    )
}

fun <T, R1, R2> List<TaskResult<T, R1>>.mapResults(transform: (R1) -> R2) = map { it.mapResult(transform) }

fun <T, R> List<TaskResult<T, R>>.filterSuccessful() = mapNotNull { taskResult ->
    taskResult.map(
        successMapper = { it },
        failureMapper = { (task, error) ->
            null.also {
                System.err.print("Task failed: ${task}\nError: ")
                error.printStackTrace()
            }
        }
    )
}

fun <T, R> List<TaskResult<T, R>>.mapToResult() = filterSuccessful().map { it.result }

sealed class TaskResult<T, R>(open val task: T) {
    abstract fun <U> mapResult(transform: (R) -> U): TaskResult<T, U>

    inline fun <U> map(
        failureMapper: (Failure<T, R>) -> U = { (_, error) -> throw error },
        successMapper: (Success<T, R>) -> U,
    ): U = when (this) {
        is Success -> successMapper(this)
        is Failure -> failureMapper(this)
    }

    data class Success<T, R>(override val task: T, val result: R) : TaskResult<T, R>(task) {
        override fun <U> mapResult(transform: (R) -> U) = Success(task, transform(result))
    }

    data class Failure<T, R>(override val task: T, val error: Exception) : TaskResult<T, R>(task) {
        override fun <U> mapResult(transform: (R) -> U) = Failure<T, U>(task, error)

        @Suppress("UNCHECKED_CAST")
        fun <U> mapToNewType() = mapResult { it as U }
    }
}
