;; original name rocket_ext.a
;;

(define (problem pb1)
  (:domain logistics)
  (:requirements :strips :typing) 
  (:objects mxf - package
	    avrim - package
	    alex - package
	    jason - package
	    pencil - package
	    paper - package
	    airplane1 - airplane
	    airplane2 - airplane
	    lon-airport - airport
	    par-airport -  airport
	    jfk-airport -  airport
	    bos-airport -  airport)
  (:init (at airplane1 jfk-airport)
	 (at airplane2 bos-airport)
	 (at mxf par-airport)
	 (at avrim par-airport)
	 (at alex par-airport)
	 (at jason jfk-airport)
	 (at pencil lon-airport)
	 (at paper lon-airport)
	 )
  (:goal (and 
	  (at mxf bos-airport)
	  (at avrim jfk-airport)
	  (at pencil bos-airport)
	  (at alex jfk-airport)
	  (at jason bos-airport)
	  (at paper par-airport)
	  )
	 )
  )
  