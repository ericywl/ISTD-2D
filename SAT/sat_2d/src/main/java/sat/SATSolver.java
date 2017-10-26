package sat;


import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     * null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment newEnv = new Environment();
        return solve(formula.getClauses(), newEnv);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses formula in conjunctive normal form
     * @param env     assignment of some or all variables in clauses to true or
     *                false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     * or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // trivially satisfiable if clauses is empty
        if (clauses.isEmpty()) {
            System.out.println("SATISFIABLE");
            return env;
        }

        // get smallest clause or first unit clause
        Clause smallestClause = new Clause();
        int minClauseSize = Integer.MAX_VALUE;
        for (Clause clause : clauses) {
            // not satisfiable if clause is empty
            if (clause.isEmpty()) {
                System.out.println("NOT SATISFIABLE");
                return null;
            }

            if (clause.isUnit()) {
                smallestClause = clause;
                break;
            }

            if (clause.size() < minClauseSize) {
                minClauseSize = clause.size();
                smallestClause = clause;
            }
        }

        Literal lit = smallestClause.chooseLiteral();
        Variable var = lit.getVariable();

        ImList<Clause> tempClauses = substitute(clauses, lit);
        Literal tempLiteral;
        Environment tempEnv;
        Environment solutionEnv;

        // if unit clause
        if (smallestClause.isUnit()) {
            tempEnv = (lit instanceof PosLiteral) ?
                    env.putFalse(var) : env.putTrue(var);
            return solve(tempClauses, tempEnv);
        }

        // if not unit clause
        tempEnv = env.putTrue(var);
        solutionEnv = solve(tempClauses, tempEnv);
        if (solutionEnv == null) {
            tempEnv = env.putFalse(var);
            tempLiteral = NegLiteral.make(var);
            tempClauses = substitute(tempClauses, tempLiteral);
            return solve(tempClauses, tempEnv);
        }

        return solutionEnv;
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses , a list of clauses
     * @param l       , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses, Literal l) {
        if (clauses.isEmpty()) return clauses;

        ImList<Clause> subClauses = new EmptyImList<>();
        for (Clause clause : clauses) {
            Clause reducedClause = clause.reduce(l);
            if (reducedClause != null) subClauses = subClauses.add(reducedClause);
        }

        return subClauses;
    }

}
