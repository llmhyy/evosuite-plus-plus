package regression.objectconstruction.testgeneration.example.graphcontruction.JNFE;

public enum Operator {
	/* 31 */   NEGATE(6),  NOT(6),  MULTIPLY(5),  DIVIDE(5),  MODULO(5),  ADD(4),  SUBTRACT(4),  GREATER_THAN(3),  GREATER_THAN_OR_EQUAL_TO(3),  LESS_THAN(3),  LESS_THAN_OR_EQUAL_TO(3),  EQUAL_TO(2),  NOT_EQUAL_TO(2),  AND(1),  OR(0);
	/*    */   
	/*    */   private final int priority;
	/*    */   public static final int MAX_PRIORITY = 6;
	/*    */   
	/*    */   private Operator(int paramInt)
	/*    */   {
	/* 51 */     priority = paramInt;
	/*    */   }
	/*    */   
	/*    */   public int getPriority()
	/*    */   {
	/* 55 */     return priority;}
}
