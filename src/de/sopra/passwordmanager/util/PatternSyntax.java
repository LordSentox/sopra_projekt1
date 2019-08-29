package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.strategy.ItemNamingStrategy;

import java.util.Arrays;
import java.util.Collection;
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
        boolean hasFilterWord = split.length != 1;
        patternFilter = !hasFilterWord ? PatternSyntaxFilter.NAME : PatternSyntaxFilter.getByKeyword(split[0]);
        rawPattern = split[hasFilterWord ? 1 : 0];
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

    public enum PatternSyntaxFilter implements CredentialsFilter, ItemNamingStrategy<Credentials> {
        NAME("name") {
            @Override
            public String getName(Credentials credentials) {
                return credentials.getName();
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getName().toLowerCase().contains(input.toLowerCase());
            }
        },
        USERNAME("user", "username") {
            @Override
            public String getName(Credentials credentials) {
                return credentials.getName() + " (username: " + credentials.getUserName() + ")";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getUserName().toLowerCase().contains(input.toLowerCase());
            }
        },
        WEBSITE("web", "page", "net", "website") {
            @Override
            public String getName(Credentials credentials) {
                return credentials.getName() + " (website: " + credentials.getWebsite() + ")";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getWebsite().toLowerCase().contains(input.toLowerCase());
            }
        },
        NOTES("note", "notes") {
            @Override
            public String getName(Credentials credentials) {
                return credentials.getName() + " (notes: " + credentials.getNotes()
                        .replace("\n", "|").substring(0, 20) + "...)";
            }

            @Override
            public boolean include(String input, Credentials credentials) {
                return credentials.getNotes().toLowerCase().contains(input.toLowerCase());
            }
        },
        COMMAND("cmd") {
            @Override
            public String getName(Credentials credentials) {
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
            return NAME;
        }

    }

    public interface CredentialsFilter {
        boolean include(String input, Credentials credentials);
    }

}