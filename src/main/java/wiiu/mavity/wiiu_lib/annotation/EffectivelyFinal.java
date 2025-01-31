package wiiu.mavity.wiiu_lib.annotation;

/**
 * Specifies that a field or method, while it cannot be set to final (usually due to volatile or synchronized modifiers, or just being unable to put the final keyword), is effectively to be assumed as final, even if it is not explicitly set as such.
 */
public @interface EffectivelyFinal {}