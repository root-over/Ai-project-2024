(define (domain dominio)

  (:requirements :strips :typing :universal-preconditions :existential-preconditions :negative-preconditions :disjunctive-preconditions)

  (:types
    ; box - scatola
    ; agent - agente robotico
    ; content - contenuto (bolt, tool, valve)
    ; workstation - stazione di lavoro
    ; location - locazione
    ; locatable - oggetto allocabile
    location locatable - object
    box agent workstation content - locatable
  )
  (:predicates
      (at ?x - locatable ?v - location) ; un locatable genrico si trova in una location
      (in-box ?b - box ?ct - content) ; un contenuto generico si trova in una scatola
      (empty-box ?b - box) ; svuota la scatola
      (served ?ws - workstation ?ct - content) ; la workstation è stata servita
      (carrying ?a - agent ?b - box) ; l'agente ha caricato il pacco
      (connected ?l1 ?l2 - location) ; le due locazioni sono connesse
      (with-box ?a - agent); l'agent ha delle scatole
  )

  (:action move
    :parameters (?a - agent ?l1 ?l2 - location) ; servono due locazioni e un agente
    :precondition (and
      (at ?a ?l1) ; l'agente si trova alla locazione l1?
      (connected ?l1 ?l2) ; l1 e l2 sono connessi?
    )
    :effect (and
      (not (at ?a ?l1)) ; l'agente non è più ad l1
      (at ?a ?l2) ; l'agente si trova in l2
    )
  )

  (:action pick-up
    :parameters (?a - agent ?b - box ?l - location)
    :precondition (and
      (at ?a ?l)
      (at ?b ?l)
      (not (carrying ?a ?b))
      (not (empty-box ?b))
      (not(with-box ?a))
    )
    :effect (and
      (carrying ?a ?b)
      (not (at ?b ?l))
      (with-box ?a)
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
      (not(with-box ?a))
    )
  )

    (:action fill-box
      :parameters (?a - agent ?b - box ?c - content ?l - location)
      :precondition (and
        (at ?a ?l)
        (at ?b ?l)
        (empty-box ?b)
        (at ?c ?l) ; Assicura che il contenuto sia presente
      )
      :effect (and
        (in-box ?b ?c)
        (not (empty-box ?b))
        ; Rimuovere il contenuto dalla locazione se è specificato in predicati
      )
    )

    (:action empty
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
)
