package com.javaweb.nextstep.chap02.calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int[] getSplitNums(String exp) {
        boolean hasCustomDelimiter = exp.startsWith("//");
        String delimiter;

        if(hasCustomDelimiter) {
            delimiter = String.valueOf(exp.charAt(2));
            exp = exp.substring(4);
        } else {
            delimiter = ";|,";
        }

        String[] arr = exp.split(delimiter);
        int[] result = new int[arr.length];

        for(int i=0; i<arr.length; i++) {
            int num = Integer.parseInt(arr[i]);

            if(num < 0) {
                throw new RuntimeException("입력값으로 음수가 들어올 수 없습니다.");
            }

            result[i] = num;
        }

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

    public static void main(String[] args) {
        String exp = "//,\n1,2,3";
        String exp2= "1;2;3";
        String t = "\\.";
        String t2 = ".";
        String t3;
        t3 = t;

        String[] arr = exp2.split(t);
        String[] arr2 = exp2.split(t2);

        new StringCalculator().getSplitNums(exp2);

//        Pattern pattern;
//        Matcher matcher;
//        pattern = Pattern.compile("[0-9]");
//        matcher = pattern.matcher(exp);
//
//        while(matcher.find()) {
//            int idx =  matcher.start();
//            System.out.println(exp.charAt(idx));
//        }
    }
}
