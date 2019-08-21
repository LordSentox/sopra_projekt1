package de.sopra.passwordmanager.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class Path
{

    private List<String> pathElements;
    private int current;

    public Path( String path ) {
        this( Arrays.asList( path.split( Pattern.quote( "/" ) ) ) );
    }

    public Path( List<String> pathElements ) {
        this( pathElements, pathElements.size()-1 );
    }

    public Path( List<String> pathElements, int current ) {
        this.pathElements = pathElements;
        navigate( current );
    }


    public Path getParent() {
        return new Path( pathElements, current - 1 );
    }

    public String getName() {
        return pathElements.get( current );
    }

    public Path getChild() {
        return new Path( pathElements, current + 1 );
    }

    public boolean hasChild() {
        return pathElements.size()-1 > current;
    }

    public boolean hasParent() {
        return current > 0;
    }

    public Path absolutePath() {
        return new Path( pathElements.subList( 0, current+1 ), current );
    }

    public Path subPath(int start, int target) {
        return new Path( pathElements.subList( start, target ), target-start-1);
    }

    public int length() {
        return pathElements.size();
    }

    public void navigate(int layer) {
        //current wird auf den nächstmöglichen Wert gesetzt, wenn der gegebene Wert außerhalb der möglichen Werte liegt
        this.current = layer < 0 ? 0 : ( layer >= pathElements.size() ? pathElements.size() - 1 : layer );
    }

    @Override
    public String toString() {
        return pathElements.stream()
                .collect( Collectors.joining("/") );
    }

}