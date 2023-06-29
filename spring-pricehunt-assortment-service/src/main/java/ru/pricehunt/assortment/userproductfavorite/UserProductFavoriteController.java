package ru.pricehunt.assortment.userproductfavorite;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.AddProductRequest;
import ru.pricehunt.assortment.dto.EmailRequest;
import ru.pricehunt.assortment.dto.UserProductFavoriteDTO;
import ru.pricehunt.assortment.mapper.UserProductFavoriteMapper;

@RequiredArgsConstructor
@RestController
@RequestMapping("/favorites")
@Tag(name = "User Favorites Products Controller", description = "Контроллер для работы с избранным товарами пользователей")
public class UserProductFavoriteController {
    private final UserProductFavoriteService userProductFavoriteService;
    private final UserProductFavoriteMapper userProductFavoriteMapper;

    @GetMapping
    @Operation(summary = "Список всех избранных товаров пользователя", description = "Возвращает список всех избранных товаров пользователя")
    public UserProductFavoriteDTO getUserFavoriteProductsByEmail(@RequestParam String email) {
        return userProductFavoriteMapper.userProductFavoriteToUserProductFavoriteDTO(userProductFavoriteService.findFavoritesByUserEmail(email));
    }

    @PostMapping("/add")
    @Operation(summary = "Добавление товара в избранное пользователя", description = "Добавляет товар в избранное пользователя")
    public UserProductFavoriteDTO addProductToUserFavorites(@RequestBody AddProductRequest productRequest) {
        return userProductFavoriteMapper.userProductFavoriteToUserProductFavoriteDTO(userProductFavoriteService.addProductToFavorite(productRequest.getEmail(), productRequest.getProductSlug()));
    }

    @PostMapping("/remove")
    @Operation(summary = "Удаление товара из избранного пользователя", description = "Удаляет товар из избранного пользователя")
    public UserProductFavoriteDTO removeProductFromUserFavorites(@RequestBody AddProductRequest productRequest) {
        return userProductFavoriteMapper.userProductFavoriteToUserProductFavoriteDTO(userProductFavoriteService.removeProductFromFavorite(productRequest.getEmail(), productRequest.getProductSlug()));
    }




}
