package org.vincentyeh.img2pdf.lib.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({FIELD, METHOD, PARAMETER})
public @interface NotNull {}
