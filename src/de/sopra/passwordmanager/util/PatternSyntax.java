package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Credentials;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class PatternSyntax {

    private PatternSyntaxFilter patternFilter;
    private String rawPattern;

    public PatternSyntax(String pattern) {
        String[] split = pattern.split(Pattern.quote(":"), 2);
        if (split.length == 1) {
            patternFilter = PatternSyntaxFilter.NAME;
            rawPattern = split[0];
        } else {
            patternFilter = PatternSyntaxFilter.getByKeyword(split[0]);
            rawPattern = split[1];
        }
    }

    public PatternSyntaxFilter getPatternFilter() {
        return patternFilter;
    }

    public String getRawPattern() {
        return rawPattern;
    }

    public boolean include(Credentials credentials) {
        return patternFilter == null ?
                PatternSyntaxFilter.NAME.include(rawPattern, credentials) :
                patternFilter.include(rawPattern, credentials);
    }

    public enum PatternSyntaxFilter implements CredentialsFilter, Function<Credentials, String> {
        NAME("name") {
            @Override
            public String apply(Credentials credentials) {
                return credentials.getName();
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getName().toLowerCase().contains(input.toLowerCase());
            }
        },
        USERNAME("user", "username") {
            @Override
            public String apply(Credentials credentials) {
                return credentials.getName() + " (username: " + credentials.getUserName() + ")";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getUserName().toLowerCase().contains(input.toLowerCase());
            }
        },
        WEBSITE("web", "page", "net", "website") {
            @Override
            public String apply(Credentials credentials) {
                return credentials.getName() + "(website: " + credentials.getWebsite() + ")";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getWebsite().toLowerCase().contains(input.toLowerCase());
            }
        },
        NOTES("note", "notes") {
            @Override
            public String apply(Credentials credentials) {
                return credentials.getName() + "(notes: " + credentials.getNotes().substring(0, 20) + "...)";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getNotes().toLowerCase().contains(input.toLowerCase());
            }
        },
        COMMAND("cmd") {
            @Override
            public String apply(Credentials credentials) {
                return credentials.getName();
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return false;
            }
        };

        private Collection<String> keywords;

        PatternSyntaxFilter(String... keywords) {
            this.keywords = Arrays.asList(keywords);
        }

        public Collection<String> getKeywords() {
            return keywords;
        }

        public static PatternSyntaxFilter getByKeyword(String keyword) {
            keyword = keyword.toLowerCase();
            for (PatternSyntaxFilter filter : values()) {
                if (filter.getKeywords().contains(keyword)) {
                    return filter;
                }
            }
            return null;
        }

    }

    public interface CredentialsFilter {
        boolean include(String input, Credentials credentials);
    }

}