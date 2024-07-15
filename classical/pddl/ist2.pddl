(define (problem istanza2)
  (:domain dominio)
  (:objects
    a1 a2 - agent
    c1 c2 - carrier
    b1 b2 b3  - box
    q0 q1 q2 - quantity
    bolt valve tool - content
    ws1 ws2 ws3 ws4 ws5 ws6 - workstation
    l1 l2 l3 l4 warehouse - location
  )

  (:init
    (pos ws1 l1)
    (pos ws2 l1)
    (pos ws3 l2)
    (pos ws4 l3)
    (pos ws5 l3)
    (pos ws6 l4)
    (box-vuota b1)
    (box-vuota b2)
    (box-vuota b3)
    (capacita c1 q2)
    (capacita c2 q2)
    (precedente q0 q1)
    (precedente q1 q2)
    (pos a1 warehouse)
    (pos a2 warehouse)
    (pos c1 warehouse)
    (pos c2 warehouse)
    (pos b1 warehouse)
    (pos b2 warehouse)
    (pos b3 warehouse)
    (carrello-vuoto c1)
    (carrello-vuoto c2)
    (pos bolt warehouse)
    (pos tool warehouse)
    (pos valve warehouse)
  )

  (:goal
    (and
        (servito ws1 tool)
        (servito ws1 bolt)
        (servito ws2 bolt)
        (servito ws3 valve)
        (servito ws4 tool)
        (servito ws4 valve)
        (servito ws5 valve)
        (servito ws5 bolt)
        (servito ws5 tool)
        (servito ws6 valve)
        (servito ws6 bolt)
        (servito ws6 tool)
    )
  )

)
