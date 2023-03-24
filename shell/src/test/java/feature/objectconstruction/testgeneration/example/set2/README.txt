GET_OBJ

Design
    - OuterMost has a long chain getter method (getFieldVar for each layer) to retrieve the primitive value
    - OuterMost.getValue(): layer1.getLayer2().getInnerMost().getValue();

Performance
    - EvoObj covers 100%
    - EvoSuite excepts