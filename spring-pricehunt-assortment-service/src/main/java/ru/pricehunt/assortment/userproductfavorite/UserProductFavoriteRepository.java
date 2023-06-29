package ru.pricehunt.assortment.userproductfavorite;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.UserProductCart;
import ru.pricehunt.assortment.model.UserProductFavorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductFavoriteRepository extends JpaRepository<UserProductFavorite, String> {


    default UserProductFavorite findFavortiesWithImages(String email) {
        UserProductFavorite userProductFavorite = findByUserEmail(email);
        if (userProductFavorite == null) {
            return null;
        }
        List<Product> products = userProductFavorite.getProducts();
        if (products != null) {
            userProductFavorite.setProducts(loadProductsWithImages(products));
        }
        return userProductFavorite;

    }

    UserProductFavorite findByUserEmail(String userEmail);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.images where p in :products")
    List<Product> loadProductsWithImages(List<Product> products);

}
