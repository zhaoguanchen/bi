import com.yiche.bigdata.utils.ExpressionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SplitTest {
    @Test
    public void testNull() {
        Object w = null;
        String i = (String) w;
        System.out.println(StringUtils.isNotEmpty(i));
        System.out.println(i);
    }

    @Test
    public void testIsContainChinese() {
        String str = "-+*/jwq哈哈kdnqdjnejkqnjkfw()";

        System.out.println(ExpressionUtils.isContainChinese(str));
    }

    public static void main(String[] args) {
        String column = "((uv/pve)*(pv+uv))+uv";
        if (StringUtils.isEmpty(column)) {
            return;
        }
        char[] columnChars;
        columnChars = column.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char columnChar : columnChars) {
            if (columnChar == '+'
                    || columnChar == '('
                    || columnChar == ')'
                    || columnChar == '-'
                    || columnChar == '*'
                    || columnChar == '/') {
                sb.append(",");
            } else {
                sb.append(columnChar);
            }
        }
        String result = new String(sb);
        String[] res = result.split(",");
        List<String> columnList = new ArrayList<>();
        for (String value : res) {
            if (!StringUtils.isEmpty(value)) {
                columnList.add(value);
            }
        }
        StringBuilder columnSb = new StringBuilder();
        StringJoiner columns = new StringJoiner(", ", "", " ");
        columnList.forEach(item -> {
            columns.add("sum(" + item + ")");
        });

    }

}
