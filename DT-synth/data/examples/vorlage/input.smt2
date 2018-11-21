;;Declare variables, variables for edges, variables for additional exprs
(declare-const x Int)
(declare-const y Int)
(declare-const x_dash Int)
(declare-const y_dash Int)
(declare-const x+y Int)
;; Initial Condition
(assert (and (= x 0) (= y 0)))
;; Safe Condition
(assert (<= 0 x))
;; Player 0 
(assert (= y 0))
;; Player 1
(assert (= y 1))
;; Edge Relation
(assert (or (and (= x (+ x_dash 1)) (= y_dash (- 1 y)))(and (= x (- x_dash 1))(= y_dash (- 1 y)))))
;; one expr per assert
(assert(= x+y (+ x y)))
