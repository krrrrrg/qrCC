package org.zerock.restqrpayment_2.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurant is a Querydsl query type for Restaurant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurant extends EntityPathBase<Restaurant> {

    private static final long serialVersionUID = 1207281833L;

    public static final QRestaurant restaurant = new QRestaurant("restaurant");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath address = createString("address");

    public final StringPath businessType = createString("businessType");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<RestaurantImage, QRestaurantImage> imageSet = this.<RestaurantImage, QRestaurantImage>createSet("imageSet", RestaurantImage.class, QRestaurantImage.class, PathInits.DIRECT2);

    public final SetPath<Menu, QMenu> menuSet = this.<Menu, QMenu>createSet("menuSet", Menu.class, QMenu.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath name = createString("name");

    public final StringPath ownerId = createString("ownerId");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath refLink = createString("refLink");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QRestaurant(String variable) {
        super(Restaurant.class, forVariable(variable));
    }

    public QRestaurant(Path<? extends Restaurant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurant(PathMetadata metadata) {
        super(Restaurant.class, metadata);
    }

}

