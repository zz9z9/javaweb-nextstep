package com.javaweb.nextstep.chap02.calculator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringCalculatorTest {

    private static StringCalculator calculator;

    @BeforeAll
    public static void setup() {
        calculator = new StringCalculator();
    }

    @Test
    @DisplayName("표현식에 하나의 숫자만 있으면 그대로 리턴한다")
    public void onlyOneNumber() {
        String exp = "12";
        int[] result1 = calculator.getSplitNums(exp);
        int[] expectResult1 = new int[]{12};

        assertThat(Arrays.equals(result1, expectResult1)).isTrue();
    }

    @Test
    @DisplayName("쉼표 또는 콜론을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 숫자를 분리")
    public void splitNumsByDefaultDelimiter() {
        String exp = "1,2,3,10;4";
        String exp2 = "1;2;3;4";
        int[] result1 = calculator.getSplitNums(exp);
        int[] expectResult1 = new int[]{1,2,3,10,4};
        int[] result2 = calculator.getSplitNums(exp2);
        int[] expectResult2 = new int[]{1,2,3,4};

        assertThat(Arrays.equals(result1, expectResult1)).isTrue();
        assertThat(Arrays.equals(result2, expectResult2)).isTrue();
    }

    @Test
    @DisplayName("쉼표, 콜론 이외의 커스텀 구분자를 지정하여 숫자를 분리할 수 있다")
    public void splitNumsByCustomDelimiter() {
        String exp = "///\n1/2/3";
        int[] result1 = calculator.getSplitNums(exp);
        int[] expectResult1 = new int[]{1,2,3};

        assertThat(Arrays.equals(result1, expectResult1)).isTrue();
    }

    @Test
    @DisplayName("각 숫자의 합을 반환한다")
    public void addNums() {
        String exp = "1;2;3;4";
        assertThat(calculator.addNums(exp)).isEqualTo(10);
    }

    @Test
    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException으로 예외처리")
    public void negativeNumException() {
        String exp = "1,-2,3";
        assertThatThrownBy(() -> calculator.getSplitNums(exp))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("음수");
    }
}
