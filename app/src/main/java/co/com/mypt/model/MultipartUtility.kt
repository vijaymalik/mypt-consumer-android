package com.cleaningservice.handler

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.ArrayList

class MultipartUtility(requestURL: String, private val charset: String, authtoken: String) {
    private val boundary: String
    private val httpConn: HttpURLConnection
    private val outputStream: OutputStream
    private val writer: PrintWriter

    /**
     * Adds a form field to the request
     *
     * @param name field name
     * @param value field value
     */
    fun addFormField(name: String, value: String) {
        Log.e("param===>", "$name ==================== $value")
        writer.append("--$boundary").append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"$name\"").append(LINE_FEED)
        writer.append("Content-Type: text/plain; charset=$charset").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName name attribute in <input type="file" name="..."></input>
     * @param uploadFile a File to be uploaded
     */
    @Throws(IOException::class)
    fun addFilePart(fieldName: String, uploadFile: File) {
        val fileName = uploadFile.name
        Log.e("param===>", "$fieldName ==================== $uploadFile")
        writer.append("--$boundary").append(LINE_FEED)
        writer.append(
            "Content-Disposition: form-data; name=\"" + fieldName
                    + "\"; filename=\"" + fileName + "\""
        )
            .append(LINE_FEED)
        writer.append(
            (
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
        )
            .append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()
        val inputStream = FileInputStream(uploadFile)
        val buffer = ByteArray(4096)
        var bytesRead = -1
        while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        inputStream.close()
        writer.append(LINE_FEED)
        writer.flush()
    }

    @Throws(IOException::class)
    fun addFilePartArray(fieldName: String, uploadFile: ArrayList<File>) {
        for (i in uploadFile.indices) {
            if (uploadFile[i] == null) {
                return
            }
            Log.e("param===>", fieldName + " ==================== " + uploadFile[i])
            val fileName = uploadFile[i]!!.name
            writer.append("--$boundary").append(LINE_FEED)
            writer.append(
                ("Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
            )
                .append(LINE_FEED)
            writer.append(
                (
                        "Content-Type: "
                                + URLConnection.guessContentTypeFromName(fileName))
            )
                .append(LINE_FEED)
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
            writer.append(LINE_FEED)
            writer.flush()
            val inputStream = FileInputStream(uploadFile[i])
            val buffer = ByteArray(4096)
            var bytesRead = -1
            while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputStream.close()
            writer.append(LINE_FEED)
            writer.flush()
        }
    }

    /**
     * Adds a header field to the request.
     *
     * @param name - name of the header field
     * @param value - value of the header field
     */
    fun addHeaderField(name: String, value: String) {
        writer.append("$name : $value").append(LINE_FEED)
        Log.e("header===>", "$name ==================== $value")

        writer.flush()
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned status OK, otherwise an
     * exception is thrown.
     */
    @Throws(IOException::class)
    fun finish(): String {
        val response = StringBuffer()
        writer.append(LINE_FEED).flush()
        writer.append("--$boundary--").append(LINE_FEED)
        writer.close()

        // checks server's status code first
        val status = httpConn.responseCode
        if (status == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(
                InputStreamReader(
                    httpConn.inputStream
                )
            )
            var line: String? = null
            while ((reader.readLine().also { line = it }) != null) {
                response.append(line)
            }
            reader.close()
            httpConn.disconnect()
        } else {
            throw IOException("Server returned non-OK status: $status")
        }
        return response.toString()
    }

    companion object {
        private val LINE_FEED = "\r\n"
    }

    /**
     * This constructor initializes a new HTTP POST request with content type is set to
     * multipart/form-data
     */
    init {

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "==="
        val auth = "Bearer " + "1|Uqgrj8d1D1AERWmQtYhe1rnIgkXJAg1kJ57FBvjX"
        val url = URL(requestURL)
        Log.e("URL", "URL : $requestURL")
        httpConn = url.openConnection() as HttpURLConnection
        httpConn.connectTimeout = 5000
        httpConn.useCaches = false
        httpConn.doOutput = true // indicates POST method
        httpConn.doInput = true
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent")
        httpConn.setRequestProperty("Test", "Bonjour")
        httpConn.setRequestProperty("Accept", "application/json")
        httpConn.setRequestProperty("Authorization", authtoken)
        outputStream = httpConn.outputStream
        writer = PrintWriter(
            OutputStreamWriter(outputStream, charset),
            true
        )
    }
}