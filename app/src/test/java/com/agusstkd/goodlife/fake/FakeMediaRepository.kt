package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.repository.MediaRepository

/**
 * Fake de [MediaRepository] para tests unitarios.
 */
class FakeMediaRepository : MediaRepository {

    var uploadResult: Result<String> = Result.Success(DEFAULT_URL)
    var lastImage: LocalImageRef? = null
        private set
    var lastFolder: String? = null
        private set
    var uploadCallCount = 0
        private set

    override suspend fun uploadImage(image: LocalImageRef, folder: String): Result<String> {
        uploadCallCount++
        lastImage = image
        lastFolder = folder
        return uploadResult
    }

    companion object {
        const val DEFAULT_URL = "https://cdn.goodlife.app/profile.jpg"
    }
}
