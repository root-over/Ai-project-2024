(define (domain dominio)
  (:requirements :strips :typing)
  (:types
    location locatable - object
    box agent workstation content carrier - locatable
    capacity-number - object
    bolt tool valve - content
  )

  (:predicates
    (at ?x - locatable ?v - location)
    (in-box ?b - box ?ct - content)
    (empty-box ?b - box)
    (served ?ws - workstation ?ct - content)
    (carrying ?a - agent ?b - box)
    (connected ?l1 ?l2 - location)
    (in-carrier ?b - box ?c - carrier)
    (empty-carrier ?c - carrier)
    (carrier-at-capacity ?c - carrier ?cap - capacity-number)
    (capacity-predecessor ?cap1 ?cap2 - capacity-number)
    (capacity ?c - carrier ?cap - capacity-number)
  )

  (:action move
    :parameters (?a - agent ?l1 ?l2 - location)
    :precondition (and
      (at ?a ?l1)
      (connected ?l1 ?l2)
    )
    :effect (and
      (not (at ?a ?l1))
      (at ?a ?l2)
    )
  )

  (:action move-with-box
    :parameters (?a - agent ?b - box ?l1 ?l2 - location)
    :precondition (and
      (at ?a ?l1)
      (at ?b ?l1)
      (carrying ?a ?b)
      (connected ?l1 ?l2)
    )
    :effect (and
      (not (at ?a ?l1))
      (at ?a ?l2)
      (not (at ?b ?l1))
      (at ?b ?l2)
    )
  )

  (:action move-with-carrier
    :parameters (?a - agent ?c - carrier ?l1 ?l2 - location)
    :precondition (and
      (at ?a ?l1)
      (at ?c ?l1)
      (connected ?l1 ?l2)
    )
    :effect (and
      (not (at ?a ?l1))
      (not (at ?c ?l1))
      (at ?a ?l2)
      (at ?c ?l2)
    )
  )

  (:action pick-up
    :parameters (?a - agent ?b - box ?l - location)
    :precondition (and
      (at ?a ?l)
      (at ?b ?l)
      (not (carrying ?a ?b))
    )
    :effect (and
      (carrying ?a ?b)
      (not (at ?b ?l))
    )
  )

  (:action drop
    :parameters (?a - agent ?b - box ?l - location)
    :precondition (and
      (at ?a ?l)
      (carrying ?a ?b)
    )
    :effect (and
      (not (carrying ?a ?b))
      (at ?b ?l)
    )
  )

    (:action fill-box
      :parameters (?a - agent ?b - box ?c - content ?l - location)
      :precondition (and
        (at ?a ?l)
        (at ?b ?l)
        (empty-box ?b)
        (at ?c ?l) ;; Assicura che il contenuto sia presente
      )
      :effect (and
        (in-box ?b ?c)
        (not (empty-box ?b))
        ;; Rimuovere il contenuto dalla locazione se è specificato in predicati
      )
    )


    (:action empty-box
      :parameters (?a - agent ?b - box ?c - content ?ws - workstation ?l - location)
      :precondition (and
        (at ?a ?l)
        (at ?b ?l)
        (at ?ws ?l)
        (in-box ?b ?c)
      )
      :effect (and
        (served ?ws ?c)
        (empty-box ?b)
        (not (in-box ?b ?c))
      )
    )

  (:action load-box-to-carrier
    :parameters (?a - agent ?b - box ?c - carrier ?l - location ?cap1 ?cap2 - capacity-number)
    :precondition (and
      (at ?a ?l)
      (at ?b ?l)
      (at ?c ?l)
      (empty-carrier ?c)
      (capacity ?c ?cap1)
      (capacity-predecessor ?cap1 ?cap2)
    )
    :effect (and
      (not (at ?b ?l))
      (in-carrier ?b ?c)
      (capacity ?c ?cap2)
      (not (capacity ?c ?cap1))
    )
  )

  ;(:action unload-box-from-carrier
  ;  :parameters (?a - agent ?b - box ?c - carrier ?l - location ?cap1 ?cap2 - capacity-number)
  ;  :precondition (and
  ;    (at ?a ?l)
  ;    (in-carrier ?b ?c)
  ;    (capacity ?c ?cap2)
  ;    (capacity-predecessor ?cap1 ?cap2)
  ;  )
  ;  :effect (and
  ;    (at ?b ?l)
  ;    (not (in-carrier ?b ?c))
  ;    (capacity ?c ?cap1)
  ;    (not (capacity ?c ?cap2))
  ;    (when (capacity-predecessor cap0 ?cap1)
  ;      (empty-carrier ?c))
  ;  )
  ;)
)
