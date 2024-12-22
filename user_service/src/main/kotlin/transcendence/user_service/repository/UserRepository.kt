package transcendence.user_service.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import transcendence.user_service.model.User

interface UserRepository: CoroutineCrudRepository<User, Int> {
    suspend fun existsUserByUsernameOrEmail(username: String, email: String): Boolean
}