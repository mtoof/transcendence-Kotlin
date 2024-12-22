package transcendence.user_service.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("userprofile")
data class User(
    @Id val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val avatar: ByteArray
)
