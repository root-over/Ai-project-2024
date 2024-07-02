(define (problem instance1)
  (:domain dominio)
  (:objects
    a1 - agent
    central_warehouse l1 l2 l3 - location
    b1 b2 b3 b4 b5 - box
    bolt valve tool - content
    ws1 ws2 ws3 - workstation
  )

  (:init
    (at a1 central_warehouse)
    (at b1 central_warehouse)
    (at b2 central_warehouse)
    (at b3 central_warehouse)
    (at b4 central_warehouse)
    (at b5 central_warehouse)
    (at bolt central_warehouse)
    (at valve central_warehouse)
    (at tool central_warehouse)
    (at ws1 l1)
    (at ws2 l2)
    (at ws3 l2)
    (empty-box b1)
    (empty-box b2)
    (empty-box b3)
    (empty-box b4)
    (empty-box b5)
    (connected central_warehouse l1)
    (connected central_warehouse l2)
    (connected central_warehouse l3)
    (connected l1 central_warehouse)
    (connected l2 central_warehouse)
    (connected l3 central_warehouse)
  )

  (:goal
    (and
      (served ws1 bolt)
      (served ws1 valve)
      (served ws2 tool)
      (served ws3 bolt)
    )
  )
)
