package org.vincentyeh.img2pdf.lib.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * Indicates that the annotated element must not be {@code null}.
 *
 * <p>This is a documentation-only annotation retained at the class level
 * ({@link RetentionPolicy#CLASS}). It carries no runtime semantics and is not
 * visible via reflection. Its sole purpose is to communicate nullability
 * contracts to developers and static-analysis tools.</p>
 *
 * <p>This annotation may be applied to:</p>
 * <ul>
 *   <li>Method parameters — the caller must not pass {@code null}.</li>
 *   <li>Method return values — the method guarantees it never returns {@code null}.</li>
 *   <li>Fields — the field must not hold {@code null} after construction.</li>
 * </ul>
 *
 * <p>Violation of the contract expressed by this annotation is undefined behaviour;
 * implementations may throw {@link IllegalArgumentException} or
 * {@link NullPointerException} at their discretion.</p>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({FIELD, METHOD, PARAMETER})
public @interface NotNull {}
