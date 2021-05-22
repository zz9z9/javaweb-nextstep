package com.javaweb.nextstep.chap02.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int[] toNumArray(String[] strArr) {
        int[] nums = new int[strArr.length];

        for(int i=0; i<strArr.length; i++) {
            int num = Integer.parseInt(strArr[i]);

            if(num < 0) {
                throw new RuntimeException("입력값으로 음수가 들어올 수 없습니다.");
            }

            nums[i] = num;
        }

        return nums;
    }

    public int[] getSplitNums(String exp) {
        boolean hasCustomDelimiter = exp.startsWith("//");
        String delimiter = ";|,";

        if(hasCustomDelimiter) {
            Pattern pattern = Pattern.compile("//(.)\n(.*)");
            Matcher matcher = pattern.matcher(exp);

            if(matcher.find()) {
                delimiter = matcher.group(1);
                exp = matcher.group(2);
            }
        }

        String[] arr = exp.split(delimiter);
        int[] result = toNumArray(arr);

        return result;
    }

    public int addNums(String exp) {
        int[] nums = getSplitNums(exp);
        int sum = 0;

        for(int num : nums) {
            sum+=num;
        }

        return sum;
    }
}
