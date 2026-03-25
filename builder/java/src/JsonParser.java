import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public static Map<Integer, List<Integer>> parseSnapshot(String json) {
        Map<Integer, List<Integer>> teams = new LinkedHashMap<Integer, List<Integer>>();

        String compact = json.replace("\n", "").replace("\r", "").replace("\t", "").trim();

        int teamsKey = compact.indexOf("\"teams\"");
        if (teamsKey == -1) {
            throw new RuntimeException("Invalid snapshot: missing 'teams'");
        }

        int start = compact.indexOf("{", teamsKey);
        int end = findMatchingBrace(compact, start);

        String body = compact.substring(start + 1, end).trim();

        int pos = 0;
        while (pos < body.length()) {

            while (pos < body.length() && (body.charAt(pos) == ',' || body.charAt(pos) == ' ')) {
                pos++;
            }

            if (pos >= body.length()) break;

            int keyStart = body.indexOf('"', pos);
            int keyEnd = body.indexOf('"', keyStart + 1);

            int teamId = Integer.parseInt(body.substring(keyStart + 1, keyEnd));

            int arrayStart = body.indexOf('[', keyEnd);
            int arrayEnd = findMatchingBracket(body, arrayStart);

            String arrayBody = body.substring(arrayStart + 1, arrayEnd);

            List<Integer> players = parseArray(arrayBody);

            teams.put(teamId, players);

            pos = arrayEnd + 1;
        }

        return teams;
    }

    private static List<Integer> parseArray(String body) {
        List<Integer> list = new ArrayList<Integer>();

        if (body.trim().isEmpty()) return list;

        String[] parts = body.split(",");

        for (String p : parts) {
            list.add(Integer.parseInt(p.trim()));
        }

        return list;
    }

    private static int findMatchingBrace(String s, int pos) {
        int depth = 0;
        for (int i = pos; i < s.length(); i++) {
            if (s.charAt(i) == '{') depth++;
            if (s.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static int findMatchingBracket(String s, int pos) {
        int depth = 0;
        for (int i = pos; i < s.length(); i++) {
            if (s.charAt(i) == '[') depth++;
            if (s.charAt(i) == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }
}
