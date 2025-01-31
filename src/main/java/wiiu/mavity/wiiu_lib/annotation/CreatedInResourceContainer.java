package wiiu.mavity.wiiu_lib.annotation;

import wiiu.mavity.wiiu_lib.util.process.ResourceContainer;

import java.lang.annotation.*;

/**
 * Any field with this annotation is specified to be set in {@link ResourceContainer#createResources()}, and after which it is effectively assumed to be final.
 */
@EffectivelyFinal
@Target(ElementType.FIELD)
public @interface CreatedInResourceContainer {}