(define (domain dominio)
  (:requirements :typing :strips :durative-actions :negative-preconditions)
  (:types
        location locatable - object
        box agent carrier workstation content - locatable
        quantity - object
  )

  (:predicates
     (box_vuota ?b - box)
     (capacita ?c - carrier ?cn - quantity)
     (pos ?x - locatable ?v - location)
     (nella_scatola ?b - box ?ct - content)
     (servito ?p - workstation ?c - content)
     (sul_carrello ?b - box ?c - carrier)
     (carrello_vuoto ?c - carrier)
     (precedente ?cn1 ?cn2 - quantity)
     (libero_agente ?a - agent )
  )

  (:durative-action muovi
    :parameters (?a - agent ?l1 ?l2 - location ?c - carrier)
    :duration ( = ?duration 5)
    :condition (and
      (at start (libero_agente ?a))
        (at start (pos ?c ?l1))
        (at start (pos ?a ?l1))
        (at start (not (carrello_vuoto ?c)))
      )
    :effect (and
        (at start(not(libero_agente ?a)))
        (at start(not (pos ?c ?l1)))
        (at start(not (pos ?a ?l1)))
        (at end(pos ?c ?l2))
        (at end(pos ?a ?l2))
        (at end(libero_agente ?a))
      )
  )

  (:durative-action muovi_agente
      :parameters (?a - agent ?l1 ?l2 - location)
      :duration ( =?duration 1)
      :condition (and
          (at start(libero_agente ?a))
          (at start(pos ?a ?l1))
        )
      :effect (and
          (at start(not(libero_agente ?a)))
          (at start(not (pos ?a ?l1)))
          (at end(pos ?a ?l2))
          (at end(libero_agente ?a))
        )
    )

 (:durative-action prendi
    :parameters (?a - agent  ?l - location ?c - carrier ?b - box ?q1 ?q2 - quantity)
    :duration ( =?duration 2)
    :condition (and
      (at start(libero_agente ?a))
      (over all (pos ?c ?l))
      (over all (pos ?a ?l))
      (at start (pos ?b ?l))
      (at start(capacita ?c ?q2))
      (at start(precedente ?q1 ?q2))
      )
    :effect (and
        (at start(not(libero_agente ?a)))
        (at start(not (pos ?b ?l)))
        (at end(capacita ?c ?q1))
        (at end(sul_carrello ?b ?c))
        (at end(not (capacita ?c ?q2)))
        (at end(not (carrello_vuoto ?c)))
        (at end(libero_agente ?a))
      )
  )

  (:durative-action togli
    :parameters (?a - agent ?l - location ?c - carrier  ?b - box  ?q1 ?q2 - quantity)
    :duration ( =?duration 2)
    :condition (and
        (at start(libero_agente ?a))
        (over all(pos ?c ?l))
        (over all(pos ?a ?l))
        (at start(capacita ?c ?q1))
        (at start(sul_carrello ?b ?c))
        (at start(precedente ?q1 ?q2))
      )
    :effect (and
        (at start(not(libero_agente ?a)))
        (at end(pos ?b ?l))
        (at end(capacita ?c ?q2))
        (at end(not (capacita ?c ?q1)))
        (at end(not (sul_carrello ?b ?c)))
        (at end(libero_agente ?a))
      )
  )

  (:durative-action riempi
    :parameters (?a - agent ?l - location  ?c - content ?b - box)
    :duration ( =?duration 3)
    :condition (and
          (at start(libero_agente ?a))
         (over all(pos ?a ?l))
         (over all(pos ?c ?l))
         (over all(pos ?b ?l))
         (at start(box_vuota ?b))
      )
    :effect (and
      (at start(not (libero_agente ?a)))
      (at end(not (box_vuota ?b)))
      (at end(nella_scatola ?b ?c))
      (at end(libero_agente ?a))
    )
  )

  (:durative-action svuota
    :parameters (?a - agent ?l - location ?c - content ?b - box ?p - workstation)
    :duration ( =?duration 3)
    :condition (and
      (at start(libero_agente ?a))
      (over all(pos ?a ?l))
      (over all(pos ?p ?l))
      (at start(not (box_vuota ?b)))
      (at start(nella_scatola ?b ?c))
      (over all(pos ?b ?l))
    )
    :effect (and
      (at start (not(libero_agente ?a)))
      (at end(servito ?p ?c))
      (at end(box_vuota ?b))
      (at end(not (nella_scatola ?b ?c)))
      (at end(libero_agente ?a))
       )
  )
)
