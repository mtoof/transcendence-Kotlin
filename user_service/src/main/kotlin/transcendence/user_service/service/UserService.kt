package transcendence.user_service.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import transcendence.user_service.model.User
import transcendence.user_service.repository.UserRepository
import java.lang.Thread.sleep

@Service
class UserService(private val repository: UserRepository) {
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
                username = requestedBody.username,
                password = requestedBody.password,
                email = requestedBody.email,
                avatar = requestedBody.avatar
            )
            repository.save(updateUser)
            return ResponseEntity(updateUser, HttpStatus.OK)
        }
    }
}