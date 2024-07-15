(define (domain dominio)
  (:requirements :typing :existential-preconditions :negative-preconditions :disjunctive-preconditions )
  (:types
        location locatable - object
        box agent carrier workstation content - locatable
        quantity - object
  )

  (:predicates
     (box-vuota ?b - box)
     (capacita ?ca - carrier ?q - quantity)
     (pos ?lb - locatable ?lc - location)
     (nella-scatola ?b - box ?ct - content)
     (servito ?ws - workstation ?ct - content)
     (sul-carrello ?b - box ?ca - carrier)
     (carrello-vuoto ?ca - carrier)
     (precedente ?q1 ?q2 - quantity)
  )

  (:action prendi
    :parameters (?a - agent  ?l - location ?c - carrier ?b - box ?q1 ?q2 - quantity)
    :precondition (and
        (pos ?c ?l)
        (pos ?a ?l)
        (pos ?b ?l)
        (capacita ?c ?q2)
        (precedente ?q1 ?q2)
      )
    :effect (and
        (not (pos ?b ?l))
        (capacita ?c ?q1)
        (sul-carrello ?b ?c)
        (not (capacita ?c ?q2))
        (not (carrello-vuoto ?c))
      )
  )

  (:action riempi
    :parameters (?a - agent ?l - location  ?c - content ?b - box)
    :precondition (and
         (pos ?a ?l)
         (pos ?c ?l)
         (box-vuota ?b)
         (or (exists (?car - carrier)
           (and
             (pos ?car ?l)
             (sul-carrello ?b ?car)
             )
           )
           (pos ?b ?l)
        )
      )
    :effect (and
      (not (box-vuota ?b))
      (nella-scatola ?b ?c)
    )
  )

  (:action muovi
    :parameters (?a - agent ?l1 ?l2 - location ?c - carrier)
    :precondition (and
        (pos ?c ?l1)
        (pos ?a ?l1)
        (not (carrello-vuoto ?c))
      )
    :effect (and
        (not (pos ?c ?l1))
        (not (pos ?a ?l1))
        (pos ?c ?l2)
        (pos ?a ?l2)
      )
  )

  (:action muovi-agente
      :parameters (?a - agent ?l1 ?l2 - location)
      :precondition (and
          (pos ?a ?l1)
        )
      :effect (and
          (not (pos ?a ?l1))
          (pos ?a ?l2)
        )
    )

  (:action togli
    :parameters (?a - agent ?l - location ?c - carrier  ?b - box  ?q1 ?q2 - quantity)
    :precondition (and
        (pos ?c ?l)
        (pos ?a ?l)
        (capacita ?c ?q1)
        (sul-carrello ?b ?c)
        (precedente ?q1 ?q2)
      )
    :effect (and
        (pos ?b ?l)
        (capacita ?c ?q2)
        (not (capacita ?c ?q1))
        (not (sul-carrello ?b ?c))
      )
  )


  (:action svuota
    :parameters (?a - agent ?l - location ?c - content ?b - box ?ws - workstation)
    :precondition (and
      (pos ?a ?l)
      (pos ?ws ?l)
      (not (box-vuota ?b))
      (nella-scatola ?b ?c)
      (or (exists (?car - carrier)
      (and
        (pos ?car ?l)
        (sul-carrello ?b ?car)
      )
     )
     (pos ?b ?l)
      )
    )
    :effect (and
      (box-vuota ?b)
      (servito ?ws ?c)
      (not (nella-scatola ?b ?c))
       )
  )
)
