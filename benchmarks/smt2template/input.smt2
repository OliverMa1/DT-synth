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
;; one expr per assert, left side is 0, right side is the expression
(assert(= 0 (+ x y)))
