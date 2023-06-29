package ru.pricehunt.assortment.userproductcart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.AddProductRequest;
import ru.pricehunt.assortment.dto.EmailRequest;
import ru.pricehunt.assortment.dto.UserProductCartDTO;
import ru.pricehunt.assortment.mapper.UserProductCartMapper;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Tag(name = "User Cart Controller", description = "Контроллер для работы с корзиной")
public class UserProductCartController {
    private final UserProductCartService userProductCartService;
    private final UserProductCartMapper userProductCartMapper;

    @GetMapping
    @Operation(summary = "Корзина товаров пользователя", description = "Возвращает корзину товаров пользователя")
    public UserProductCartDTO getUserCartProductsByEmail(@RequestParam String email) {
        return userProductCartMapper.userProductCartToUserProductCartDTO(userProductCartService.findCartByUserEmail(email));
    }

    @PostMapping("/add")
    @Operation(summary = "Добавление товара в корзину пользователя", description = "Добавляет товар в корзину пользователя")
    public UserProductCartDTO addProductToUserCart(@RequestBody AddProductRequest productRequest) {
        return userProductCartMapper.userProductCartToUserProductCartDTO(userProductCartService.addProductToCart(productRequest.getEmail(), productRequest.getProductSlug()));
    }

    @PostMapping("/remove")
    @Operation(summary = "Удаление товара из корзины пользователя", description = "Удаляет товар из корзины пользователя")
    public UserProductCartDTO removeProductFromUserCart(@RequestBody AddProductRequest productRequest) {
        return userProductCartMapper.userProductCartToUserProductCartDTO(userProductCartService.removeProductFromCart(productRequest.getEmail(), productRequest.getProductSlug()));
    }




}
