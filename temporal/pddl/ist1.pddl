(define (problem istanza1)
  (:domain dominio)
  (:objects
    a1 - agent
    c1 - carrier
    b1 b2 b3  - box
    q0 q1 - quantity
    bolt valve tool - content
    p1 p2 p3 p4 - workstation
    l1 l2 l3 l4 warehouse - location
  )

  (:init
    (pos p1 l1)
    (pos p2 l1)
    (pos p3 l2)
    (pos p4 l3)
    (box_vuota b1)
    (box_vuota b2)
    (box_vuota b3)
    (capacita c1 q1)
    (precedente q0 q1)
    (pos a1 warehouse)
    (pos c1 warehouse)
    (pos b1 warehouse)
    (pos b2 warehouse)
    (pos b3 warehouse)
    (carrello_vuoto c1)
    (pos bolt warehouse)
    (pos tool warehouse)
    (pos valve warehouse)
    (libero_agente a1)



  )

  (:goal
    (and
        (servito p1 tool)
        (servito p1 bolt)
        (servito p2 bolt)
        (servito p3 valve)
        (servito p4 tool)
        (servito p4 valve)

    )
  )

)
