package com.ergin.ghclient.feature.upload.impl.data.remote

import com.ergin.ghclient.feature.upload.impl.data.remote.model.UploadFileResponseDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UploadApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parse PUT upload file response contract JSON to UploadFileResponseDto`() {
        val contractJson = """
            {
              "content": {
                "name": "config.txt",
                "path": "config.txt",
                "sha": "95b983be0dbb73c6aa1e156618c264d5e0d2eed8"
              },
              "commit": {
                "sha": "761a5e52d86c069b2f61e2b2b9306e7303020381",
                "message": "Upload new configuration file"
              }
            }
        """.trimIndent()

        val responseDto: UploadFileResponseDto = json.decodeFromString(contractJson)

        assertNotNull(responseDto.content)
        assertEquals("config.txt", responseDto.content?.name)
        assertEquals("config.txt", responseDto.content?.path)
        assertEquals("95b983be0dbb73c6aa1e156618c264d5e0d2eed8", responseDto.content?.sha)

        assertNotNull(responseDto.commit)
        assertEquals("761a5e52d86c069b2f61e2b2b9306e7303020381", responseDto.commit?.sha)
        assertEquals("Upload new configuration file", responseDto.commit?.message)
    }
}
