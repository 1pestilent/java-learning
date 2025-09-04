package com.example.demo.controller;
import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController{

    private final UserServiceImpl userService ;


    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Серверная ошибка")
    })
    @PostMapping("/")
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @Parameter(description = "Создает пользователя", required = true)
            @RequestBody UserCreateDto createdUser
    ) {
        UserDto userDto = userService.createUser(createdUser);
        EntityModel<UserDto> resource = EntityModel.of(userDto);

        resource.add(linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        resource.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("update-user"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete-user"));
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Получает пользователя по уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Серверная ошибка")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(
            @PathVariable long id
    ) {
        UserDto userDto = userService.getUserById(id);
        EntityModel<UserDto> resource = EntityModel.of(userDto);

        resource.add(linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        resource.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("update-user"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete-user"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Получить всех пользователей", description = "Получает всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи получены",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "500", description = "Серверная ошибка")
    })
    @GetMapping("/")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {

        List<UserDto> users = userService.getAllUsers();

        List<EntityModel<UserDto>> userResources = users.stream()
                .map(user -> {
                    EntityModel<UserDto> resource = EntityModel.of(user);
                    resource.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("update-user"));
                    resource.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete-user"));
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(userResources);
        collection.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(collection);

    }


    @Operation(summary = "Обновить пользователя", description = "Обновляет информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный ввод"),
            @ApiResponse(responseCode = "500", description = "Серверная ошибка")
    })
    @PutMapping("/")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @Parameter(description = "Обновить пользователя", required = true)
            @RequestBody UserUpdateDto updatedUser
    ) {
        UserDto user = userService.updateUser(updatedUser);
        EntityModel<UserDto> resource = EntityModel.of(user);

        resource.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete-user"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользватель не найден"),
            @ApiResponse(responseCode = "500", description = "Серверная ошибка")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);

        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        Link createUserLink = linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user");

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header("Link", allUsersLink.toString())
                .header("Link", createUserLink.toString())
                .build();
    }
}

