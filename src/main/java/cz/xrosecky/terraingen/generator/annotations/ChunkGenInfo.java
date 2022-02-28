package cz.xrosecky.terraingen.generator.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ChunkGenInfo {
    String[] versions();


}
