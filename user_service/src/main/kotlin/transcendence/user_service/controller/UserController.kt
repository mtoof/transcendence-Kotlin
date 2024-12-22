package transcendence.user_service.controller

import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import transcendence.user_service.model.User
import transcendence.user_service.service.UserService

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {
    @GetMapping()
    suspend fun listUsers() = ResponseEntity.ok(userService.findUsers())

    @GetMapping("/{userId}")
    suspend fun findUserById(@PathVariable userId: Int) = ResponseEntity.ok(userService.findUserById(userId))

    @PostMapping()
    @Transactional
    suspend fun createUser(@RequestBody user: User, ucb: UriComponentsBuilder): ResponseEntity<String> {
     return userService.saveUser(user, ucb)
    }
}