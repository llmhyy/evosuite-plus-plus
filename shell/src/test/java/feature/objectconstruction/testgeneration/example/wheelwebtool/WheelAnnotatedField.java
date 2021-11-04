package feature.objectconstruction.testgeneration.example.wheelwebtool;

/*     */ public class WheelAnnotatedField {
/*     */    private String name;
/*     */    private String desc;
/*     */    private String signature;
/*     */    private Scope scope;
/*     */    private boolean setterAvailable;
/*     */    private boolean getterAvailable;
///*     */    private Label startLabel;
/*     */    private int localVarIndex = -1;
/*     */    private String ownerClassName;
/*     */ 
/*     */    public WheelAnnotatedField(String name, String desc, String signature, Scope scope) {
/*  43 */       this.name = name;
/*  44 */       this.scope = scope;
/*  45 */       this.desc = desc;
/*  46 */       this.signature = signature;
/*     */ 
/*  48 */       if (scope == null) {
/*  49 */          this.scope = Scope.session;      }
/*  50 */    }
/*     */ 
/*     */    public String getName() {
/*  53 */       return this.name;
/*     */    }
/*     */ 
/*     */    public Scope getScope() {
/*  57 */       return this.scope;
/*     */    }
/*     */ 
/*     */ 
/*     */    public void setScope(Scope scope) {
/*  62 */       this.scope = scope;
/*  63 */    }
/*     */ 
/*     */    public String getDesc() {
/*  66 */       return this.desc;
/*     */    }
/*     */ 
/*     */    public String getSignature() {
/*  70 */       return this.signature;
/*     */    }
/*     */ 
/*     */    public boolean isSetterAvailable() {
/*  74 */       return this.setterAvailable;
/*     */    }
/*     */ 
/*     */    public void setSetterAvailable(boolean setterAvailable) {
/*  78 */       this.setterAvailable = setterAvailable;
/*  79 */    }
/*     */ 
/*     */    public boolean isGetterAvailable() {
/*  82 */       return this.getterAvailable;
/*     */    }
/*     */ 
/*     */    public void setGetterAvailable(boolean getterAvailable) {
/*  86 */       this.getterAvailable = getterAvailable;
/*  87 */    }
/*     */ 
/*     */    public String getSetterName() {
/*  90 */       return "set" + Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1, this.name.length());
/*     */    }
/*     */ 
/*     */    public String getGetterName() {
/*  94 */       return this.desc.equals("Z") ? "is" + Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1, this.name.length()) : "get" + Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1, this.name.length());
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public boolean isGetterOrSetterForThisField(String methodName) {
/* 101 */       if (methodName.equals(this.getGetterName())) {
/* 102 */          this.getterAvailable = true;
/* 103 */          return true;
/*     */ 
/*     */ 
/* 106 */       } else if (methodName.equals(this.getSetterName())) {
/* 107 */          this.setterAvailable = true;
/* 108 */          return true;
/*     */ 
/*     */       } else {
/* 111 */          return false;
/*     */       }
/*     */    }
///*     */    public int getLoadOpcode() {
///* 115 */       if (!this.desc.equals("I") && !this.desc.equals("C") && !this.desc.equals("S") && !this.desc.equals("Z") && !this.desc.equals("B")) {         }      } else {
///* 116 */          return 21;
///* 117 */          if (this.desc.equals("J")) {
///* 118 */             return 22;
///* 119 */          } else if (this.desc.equals("F")) {
///* 120 */             return 23;
///* 121 */          } else if (this.desc.equals("D")) {
///* 122 */             return 24;
///* 123 */          } else if (!this.desc.startsWith("L") && !this.desc.startsWith("[")) {         } else {
///* 124 */             return 25;
///*     */ 
///*     */ 
///* 127 */             throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
///*     */    public int getStoreOpcode() {
///* 131 */       if (!this.desc.equals("I") && !this.desc.equals("C") && !this.desc.equals("S") && !this.desc.equals("Z") && !this.desc.equals("B")) {         }      } else {
///* 132 */          return 54;
///* 133 */          if (this.desc.equals("J")) {
///* 134 */             return 55;
///* 135 */          } else if (this.desc.equals("F")) {
///* 136 */             return 56;
///* 137 */          } else if (this.desc.equals("D")) {
///* 138 */             return 57;
///* 139 */          } else if (!this.desc.startsWith("L") && !this.desc.startsWith("[")) {         } else {
///* 140 */             return 58;
///*     */ 
///*     */ 
///* 143 */             throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
///*     */    public int getReturnOpCode() {
///* 147 */       if (!this.desc.equals("I") && !this.desc.equals("C") && !this.desc.equals("S") && !this.desc.equals("Z") && !this.desc.equals("B")) {         }      } else {
///* 148 */          return 172;
///* 149 */          if (this.desc.equals("J")) {
///* 150 */             return 173;
///* 151 */          } else if (this.desc.equals("F")) {
///* 152 */             return 174;
///* 153 */          } else if (this.desc.equals("D")) {
///* 154 */             return 175;
///* 155 */          } else if (!this.desc.startsWith("L") && !this.desc.startsWith("[")) {         } else {
///* 156 */             return 176;
///*     */ 
///*     */ 
///* 159 */             throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
///*     */    public String getWrapperType() {
///* 163 */       if (this.desc.equals("I")) {
///* 164 */          return "java/lang/Integer";
///* 165 */       } else if (this.desc.equals("J")) {
///* 166 */          return "java/lang/Long";
///* 167 */       } else if (this.desc.equals("F")) {
///* 168 */          return "java/lang/Float";
///* 169 */       } else if (this.desc.equals("D")) {
///* 170 */          return "java/lang/Double";
///* 171 */       } else if (this.desc.equals("Z")) {
///* 172 */          return "java/lang/Boolean";
///* 173 */       } else if (this.desc.equals("C")) {
///* 174 */          return "java/lang/Character";
///* 175 */       } else if (this.desc.equals("S")) {
///* 176 */          return "java/lang/Short";      } else {
///* 177 */          return this.desc.equals("B") ? "java/lang/Byte" : this.getClassName();
///*     */       }
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public String getWrapperTypeInternal() {
///* 185 */       if (this.desc.equals("I")) {
///* 186 */          return "Ljava/lang/Integer;";
///* 187 */       } else if (this.desc.equals("J")) {
///* 188 */          return "Ljava/lang/Long;";
///* 189 */       } else if (this.desc.equals("F")) {
///* 190 */          return "Ljava/lang/Float;";
///* 191 */       } else if (this.desc.equals("D")) {
///* 192 */          return "Ljava/lang/Double;";
///* 193 */       } else if (this.desc.equals("Z")) {
///* 194 */          return "Ljava/lang/Boolean;";
///* 195 */       } else if (this.desc.equals("C")) {
///* 196 */          return "Ljava/lang/Character;";
///* 197 */       } else if (this.desc.equals("S")) {
///* 198 */          return "Ljava/lang/Short;";      } else {
///* 199 */          return this.desc.equals("B") ? "Ljava/lang/Byte;" : this.desc;
///*     */       }
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public String getNarrowingMethod() {
///* 207 */       if (this.desc.equals("I")) {
///* 208 */          return "intValue";
///* 209 */       } else if (this.desc.equals("J")) {
///* 210 */          return "longValue";
///* 211 */       } else if (this.desc.equals("F")) {
///* 212 */          return "floatValue";
///* 213 */       } else if (this.desc.equals("D")) {
///* 214 */          return "doubleValue";
///* 215 */       } else if (this.desc.equals("Z")) {
///* 216 */          return "booleanValue";
///* 217 */       } else if (this.desc.equals("C")) {
///* 218 */          return "charValue";
///* 219 */       } else if (this.desc.equals("S")) {
///* 220 */          return "shortValue";
///* 221 */       } else if (this.desc.equals("B")) {
///* 222 */          return "byteValue";
///*     */ 
///*     */       } else {
///* 225 */          throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
/*     */    public boolean needsWrapping() {
/* 229 */       return !this.desc.startsWith("L") && !this.desc.startsWith("[");
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getClassName() {
/* 236 */       return !this.needsWrapping() && this.desc.length() > 2 ? this.desc.substring(1, this.desc.length() - 1) : this.desc;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    public Label getStartLabel() {
///* 244 */       return this.startLabel;
///*     */    }
///*     */ 
///*     */    public void setStartLabel(Label startLabel) {
///* 248 */       this.startLabel = startLabel;
///* 249 */    }
/*     */ 
/*     */ 
/*     */    public int getLocalVarIndex() {
/* 253 */       return this.localVarIndex;
/*     */    }
/*     */ 
/*     */    public void setLocalVarIndex(int localVarIndex) {
/* 257 */       this.localVarIndex = localVarIndex;
/* 258 */    }
/*     */ 
///*     */    public int getMaxStackSizeForSetter() {
///* 261 */       if (!this.desc.equals("I") && !this.desc.equals("C") && !this.desc.equals("S") && !this.desc.equals("Z") && !this.desc.equals("B")) {         }      } else {
///* 262 */          return 6;
///* 263 */          if (!this.desc.equals("D") && !this.desc.equals("J") && !this.desc.equals("F")) {            }         } else {
///* 264 */             return 7;
///* 265 */             if (!this.desc.startsWith("L") && !this.desc.startsWith("[")) {            } else {
///* 266 */                return 6;
///*     */ 
///* 268 */                throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
///*     */    public int getMaxStackSizeForGetter() {
///* 272 */       if (!this.desc.equals("I") && !this.desc.equals("C") && !this.desc.equals("S") && !this.desc.equals("Z") && !this.desc.equals("B")) {         }      } else {
///* 273 */          return 7;
///* 274 */          if (!this.desc.equals("D") && !this.desc.equals("J") && !this.desc.equals("F")) {            }         } else {
///* 275 */             return 7;
///* 276 */             if (!this.desc.startsWith("L") && !this.desc.startsWith("[")) {            } else {
///* 277 */                return 7;
///*     */ 
///* 279 */                throw new IllegalStateException("Field description is of unknown type.");
///*     */       }
///*     */    }
/*     */    public boolean isWideNativeType() {
/* 283 */       return this.desc.equals("D") || this.desc.equals("J") || this.desc.equals("F");
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getOwnerClassName() {
/* 291 */       return this.ownerClassName;
/*     */    }
/*     */ 
/*     */    public void setOwnerClassName(String ownerClassName) {
/* 295 */       this.ownerClassName = ownerClassName;
/* 296 */    }
/*     */ }