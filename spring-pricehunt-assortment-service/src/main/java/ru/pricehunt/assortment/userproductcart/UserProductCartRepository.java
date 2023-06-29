package ru.pricehunt.assortment.userproductcart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.UserProductCart;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserProductCartRepository extends JpaRepository<UserProductCart, String> {

    default UserProductCart findCartWithImages(String email) {
        UserProductCart userProductCart = findByUserEmail(email);
        if (userProductCart == null) {
            return null;
        }
        List<Product> products = userProductCart.getProducts();
        if (products != null) {
            userProductCart.setProducts(loadProductsWithImages(products));
        }
        return userProductCart;

    }

    UserProductCart findByUserEmail(String userEmail);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.images where p in :products")
    List<Product> loadProductsWithImages(List<Product> products);
}
