package transcendence.user_service.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import transcendence.user_service.model.User
import transcendence.user_service.repository.CroutineUserRepository
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID.randomUUID
import javax.imageio.ImageIO

@Service
class UserService(private val repository: CroutineUserRepository, @Value("\${file.upload-url}") private val uploadDir: String) {

    suspend fun findUserById(id: Int): User? =
        repository.findById(id)

    @Transactional
    suspend fun saveUser(user: User, ucb: UriComponentsBuilder): ResponseEntity<String> {
        return try {
            if (repository.existsUserByUsernameOrEmail(user.username, user.email))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "username or email is already in use!!!")
            else
            {
                val savedUser = repository.save(user)
                val locationOfSavedUser = withContext(Dispatchers.Default) {
                    ucb.path("/user/${savedUser.id}")
                        .buildAndExpand(savedUser.id)
                        .toUri()
                }
                ResponseEntity.created(locationOfSavedUser).build()
            }
        } catch (e: DataIntegrityViolationException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "username or email is already in use!!!")
        }
    }

    suspend fun findUsers(): List<User> =
        repository.findAll().toList()

    suspend fun deleteUser(id: Int): ResponseEntity<Void> {
        return if (repository.findById(id) != null) {
            repository.deleteById(id)
            ResponseEntity.noContent().build()
        }
        else
            ResponseEntity.notFound().build()
    }

    suspend fun updateUser(id: Int, requestedBody: User): ResponseEntity<User> {

        val existingUser = repository.findById(id)
        return if (existingUser == null)
            ResponseEntity.notFound().build()
        else {
            val updateUser = existingUser.copy(
                id = existingUser.id,
                username = requestedBody.username.takeIf { it.isNotEmpty() } ?: existingUser.username,
                password = requestedBody.password.takeIf {  it.isNotEmpty() } ?: existingUser.password,
                email = requestedBody.email.takeIf { it.isNotEmpty() } ?: existingUser.email
            )
            repository.save(updateUser)
            return ResponseEntity.noContent().build()
        }
    }

    suspend fun updateUserAvatar(id: Int, avatarFile: MultipartFile) :ResponseEntity<String>{
        val existingUser = findUserById(id) ?: return ResponseEntity.notFound().build()

        if (!validateImage(avatarFile)) return ResponseEntity.badRequest()
            .body("\"Invalid image file. Please upload a valid JPG, JPEG, PNG, BMP, or GIF.\"")

        val targetPath = Paths.get(uploadDir + "/" + existingUser.id, randomUUID().toString() + "-" + avatarFile.originalFilename)
        withContext(Dispatchers.IO) {
            Files.createDirectories(targetPath.parent)
        }
        avatarFile.inputStream.use { inputStream -> Files.copy(inputStream, targetPath) }
        val updateUser = existingUser.copy(
            id = existingUser.id,
            username = existingUser.username,
            password = existingUser.password,
            email = existingUser.email,
            avatar = targetPath.toString())
        repository.save(updateUser)
        return ResponseEntity.noContent().build()
    }

    private suspend fun validateImage(avatarFile: MultipartFile): Boolean{
        val allowedTypes = listOf("Image/jpg", "Image/jpeg", "Image/png", "Image/bmp", "Image/gif")
        if (avatarFile.contentType !in allowedTypes) return false

        return try
        {
            val imageStream = ByteArrayInputStream(avatarFile.bytes)
            val image: BufferedImage? = withContext(Dispatchers.IO) {
                ImageIO.read(imageStream)
            }
            image != null
        }
        catch (e: IOException)
        {
            false
        }
    }
}