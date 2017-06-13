package org.springframework.data.ebean.convert;

import io.ebean.EbeanServer;
import io.ebean.ExampleExpression;
import io.ebean.LikeType;
import org.springframework.data.domain.Example;

/**
 * Build {@link io.ebean.ExampleExpression} from {@link org.springframework.data.domain.Example}
 *
 * @author Xuegui Yuan
 */
public class ExampleExpressionBuilder {

    /**
     * Return a ExampleExpression from Spring data Example
     *
     * @param ebeanServer
     * @param example
     * @param <T>
     * @return
     */
    public static <T> ExampleExpression exampleExpression(EbeanServer ebeanServer, Example<T> example) {
        LikeType likeType;
        switch (example.getMatcher().getDefaultStringMatcher()) {
            case EXACT:
                likeType = LikeType.EQUAL_TO;
                break;
            case CONTAINING:
                likeType = LikeType.CONTAINS;
                break;
            case STARTING:
                likeType = LikeType.STARTS_WITH;
                break;
            case ENDING:
                likeType = LikeType.ENDS_WITH;
                break;
            default:
                likeType = LikeType.RAW;
                break;
        }
        return ebeanServer.getExpressionFactory().exampleLike(example.getProbe(),
                example.getMatcher().isIgnoreCaseEnabled(),
                likeType);
    }
}
