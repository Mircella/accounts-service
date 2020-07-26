package kz.mircella.accounts.infrastructure.web.api

import com.fasterxml.jackson.annotation.JsonCreator
import kz.mircella.accounts.domain.user.CreateUserCommand
import kz.mircella.accounts.domain.user.User
import kz.mircella.accounts.domain.user.UserService
import kz.mircella.accounts.infrastructure.web.ControllerUtils
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/users")
    fun insertUsers(@RequestBody users: List<CreateUserCommand>): CompletableFuture<ApiResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val result = userService.saveUsers(users)
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @GetMapping("/users")
    fun getUsers(
            @RequestParam(required = false, defaultValue = "10") size: Int
    ): CompletableFuture<UsersResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val users = userService.getAllUsers(size).map { UserResponse.fromUser(it) }
            UsersResponse(users)
        })
    }

    @GetMapping("/users/query")
    fun getUsersByQuery(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<UsersResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val users = userService.findByQuery(searchQuery.query).map { UserResponse.fromUser(it) }
            UsersResponse(users)
        })
    }

    @GetMapping("/users/login")
    fun getUsersByLogin(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<UserResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val user = userService.findByLogin(searchQuery.query)
            UserResponse.fromUser(user)
        })
    }

    @PostMapping("/users/index/create")
    fun createUsersIndex(): CompletableFuture<ApiResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val result = userService.createUsersIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/users/index/delete")
    fun deleteUsersIndex(): CompletableFuture<ApiResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val result = userService.deleteUsersIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/users/index/clear")
    fun clearUsersIndex(): CompletableFuture<ApiResponse> {
        return ControllerUtils.unwrappedAsync(Supplier {
            val result = userService.deleteAllUsers()
            ApiResponse.fromIndexOperationResult(result)
        })
    }
}

data class UsersResponse(val users: List<UserResponse>)

data class UserResponse private constructor(val name: String, val surname: String) {

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromUser(user: User) = UserResponse(user.name, user.surname)
    }
}