package feature.smartseed.example.empirical;

import java.io.StreamTokenizer;

public class StreamTokenizers {
	
	static void printTokenizer(StreamTokenizer var0) {
		if (var0.ttype == -2) {
			System.out.print(" ");
		}

		if (var0.ttype == JSJshopVars.leftPar) {
			System.out.print("( ");
		}
		if (var0.ttype == JSJshopVars.rightPar) {
			System.out.print(") ");
		}
		if (var0.ttype == JSJshopVars.colon) {
			System.out.print(": ");
		}
		if (var0.ttype == JSJshopVars.dot) {
			System.out.print(". ");
		}
		if (var0.ttype == JSJshopVars.semicolon) {
			System.out.print("; ");
		}
		if (var0.ttype == JSJshopVars.apostrophe) {
			System.out.print("' ");
		}
		if (var0.ttype == JSJshopVars.exclamation) {
			System.out.print("! ");
		}
		if (var0.ttype == JSJshopVars.interrogation) {
			System.out.print("? ");
		}
		if (var0.ttype == JSJshopVars.percent) {
			System.out.print("% ");
		}
		if (var0.ttype == JSJshopVars.minus) {
			System.out.print("- ");
		}
		if (var0.ttype == JSJshopVars.lessT) {
			System.out.print("< ");
		}
		if (var0.ttype == JSJshopVars.equalT) {
			System.out.print("= ");
		}
		if (var0.ttype == JSJshopVars.greaterT) {
			System.out.print("> ");
		}
		if (var0.ttype == JSJshopVars.plus) {
			System.out.print("+ ");
		}
		if (var0.ttype == JSJshopVars.coma) {
			System.out.print(", ");
		}
		if (var0.ttype == JSJshopVars.astherisk) {
			System.out.print("* ");
		}
		if (var0.ttype == JSJshopVars.slash) {
			System.out.print("/ ");
		}
		if (var0.ttype == JSJshopVars.backquote) {
			System.out.print("` ");
		}
		if (var0.ttype == JSJshopVars.rightBrac) {
			System.out.print("] ");
		}
		if (var0.ttype == JSJshopVars.leftBrac) {
			System.out.print("[ ");
		}
		if (var0.ttype == JSJshopVars.verticalL) {
			System.out.print("| ");
		}
		if (var0.ttype == -3) {
			System.out.print(" ");
		}
	}
}
