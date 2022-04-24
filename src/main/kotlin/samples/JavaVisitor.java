package samples;

// really creepy implementation of acyclic visitor

interface JavaVisitor {}

interface ExpressionVisitor extends JavaVisitor {
    void visit(Expression obj);
}

interface DoubleExpressionVisitor extends JavaVisitor
{
    void visit(DoubleExpression obj);
}

interface AdditionExpressionVisitor extends JavaVisitor
{
    void visit(AdditionExpression obj);
}

abstract class Expression
{
    // optional
    public void accept(JavaVisitor visitor)
    {
        if (visitor instanceof ExpressionVisitor) {
            ((ExpressionVisitor) visitor).visit(this);
        }
    }
}

class DoubleExpression extends Expression
{
    public double value;

    public DoubleExpression(double value) {
        this.value = value;
    }

    @Override
    public void accept(JavaVisitor visitor) {
        if (visitor instanceof ExpressionVisitor) {
            ((ExpressionVisitor) visitor).visit(this);
        }
    }
}

class AdditionExpression extends Expression
{
    public Expression left, right;

    public AdditionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(JavaVisitor visitor) {
        if (visitor instanceof AdditionExpressionVisitor) {
            ((AdditionExpressionVisitor) visitor).visit(this);
        }
    }
}

class ExpressionPrinter implements
        DoubleExpressionVisitor,
        AdditionExpressionVisitor
{
    private StringBuilder sb = new StringBuilder();

    @Override
    public void visit(DoubleExpression obj) {
        sb.append(obj.value);
    }

    @Override
    public void visit(AdditionExpression obj) {
        sb.append('(');
        obj.left.accept(this);
        sb.append('+');
        obj.right.accept(this);
        sb.append(')');
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}

class AcyclicVisitorDemo
{
    public static void main(String[] args) {
        AdditionExpression e = new AdditionExpression(
                new DoubleExpression(1),
                new AdditionExpression(
                        new DoubleExpression(2),
                        new DoubleExpression(3)
                )
        );
        ExpressionPrinter ep = new ExpressionPrinter();
        ep.visit(e);
        System.out.println(ep.toString());
    }
}

