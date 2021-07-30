package net.mikaelzero.mojito.loader.fresco

import java.io.*
import java.lang.Exception
import kotlin.Throws
import kotlin.jvm.JvmOverloads

object IOUtils {
    /**
     * The default buffer size ({@value}) to use for
     * [.copyLarge]
     * and
     * [.copyLarge]
     */
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4

    /**
     * The default buffer size to use for the skip() methods.
     */
    private const val SKIP_BUFFER_SIZE = 2048

    /**
     * Represents the end-of-file (or stream).
     *
     * @since 2.5 (made public)
     */
    const val EOF = -1
    // copy from InputStream
    //-----------------------------------------------------------------------
    /**
     * Copies bytes from an `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * Large streams (over 2GB) will return a bytes copied value of
     * `-1` after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the `copyLarge(InputStream, OutputStream)` method.
     *
     * @param input  the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream): Int {
        val count = copyLarge(input, output)
        return if (count > Int.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    /**
     * Copies bytes from an `InputStream` to an `OutputStream` using an internal buffer of the
     * given size.
     *
     *
     * This method buffers the input internally, so there is no need to use a `BufferedInputStream`.
     *
     *
     *
     * @param input      the `InputStream` to read from
     * @param output     the `OutputStream` to write to
     * @param bufferSize the bufferSize used to copy from the input to the output
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.5
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream, bufferSize: Int): Long {
        return copyLarge(input, output, ByteArray(bufferSize))
    }

    /**
     * Copies bytes from a large (over 2GB) `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * The buffer size is given by [.DEFAULT_BUFFER_SIZE].
     *
     * @param input  the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream): Long {
        return copy(input, output, DEFAULT_BUFFER_SIZE)
    }

    /**
     * Copies bytes from a large (over 2GB) `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method uses the provided buffer, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     *
     * @param input  the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream, buffer: ByteArray?): Long {
        var count: Long = 0
        var n: Int
        while (EOF != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }
    // copy from Reader
    //-----------------------------------------------------------------------
    /**
     * Copies chars from a `Reader` to a `Writer`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedReader`.
     *
     *
     * Large streams (over 2GB) will return a chars copied value of
     * `-1` after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the `copyLarge(Reader, Writer)` method.
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    @Throws(IOException::class)
    fun copy(input: Reader, output: Writer): Int {
        val count = copyLarge(input, output)
        return if (count > Int.MAX_VALUE) {
            -1
        } else count.toInt()
    }
    /**
     * Copies chars from a large (over 2GB) `Reader` to a `Writer`.
     *
     *
     * This method uses the provided buffer, so there is no need to use a
     * `BufferedReader`.
     *
     *
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    /**
     * Copies chars from a large (over 2GB) `Reader` to a `Writer`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedReader`.
     *
     *
     * The buffer size is given by [.DEFAULT_BUFFER_SIZE].
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copyLarge(input: Reader, output: Writer, buffer: CharArray? = CharArray(DEFAULT_BUFFER_SIZE)): Long {
        var count: Long = 0
        var n: Int
        while (EOF != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    /**
     * Closes an `InputStream` unconditionally.
     *
     *
     * Equivalent to [InputStream.close], except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param inputStream `InputStream`
     */
    fun closeQuietly(inputStream: InputStream?) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (ignored: Exception) {
                // ignored
            }
        }
    }

    /**
     * Closes an `OutputStream` unconditionally.
     *
     *
     * Equivalent to [OutputStream.close], except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param outputStream `OutputStream`
     */
    fun closeQuietly(outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                outputStream.close()
            } catch (ignored: Exception) {
                // ignored
            }
        }
    }

    /**
     * Closes an `BufferedReader` unconditionally.
     *
     *
     * Equivalent to [BufferedReader.close], except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param bufferedReader `BufferedReader`
     */
    fun closeQuietly(bufferedReader: BufferedReader?) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close()
            } catch (ignored: Exception) {
                // ignored
            }
        }
    }
}