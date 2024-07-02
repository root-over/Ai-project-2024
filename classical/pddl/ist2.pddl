(define (problem instance2)
  (:domain dominio)
  (:objects
    a1 a2 - agent
    c1 c2 - carrier
    central_warehouse l1 l2 l3 l4 - location
    b1 b2 b3 b4 - box
    bolt valve tool - content
    ws1 ws2 ws3 ws4 - workstation
    cap0 cap1 cap2 - capacity-number
  )

  (:init
    (capacity-predecessor cap0 cap1)
    (capacity-predecessor cap1 cap2)
    (capacity c1 cap2)
    (capacity c2 cap2)
    (at a1 central_warehouse)
    (at a2 central_warehouse)
    (at c1 central_warehouse)
    (at c2 central_warehouse)
    (at b1 central_warehouse)
    (at b2 central_warehouse)
    (at b3 central_warehouse)
    (at b4 central_warehouse)
    (at bolt central_warehouse)
    (at valve central_warehouse)
    (at tool central_warehouse)
    (at ws1 l1)
    (at ws2 l2)
    (at ws3 l3)
    (at ws4 l4)
    (empty-box b1)
    (empty-box b2)
    (empty-box b3)
    (empty-box b4)
    (empty-carrier c1)
    (empty-carrier c2)
    (connected central_warehouse l1)
    (connected central_warehouse l2)
    (connected central_warehouse l3)
    (connected central_warehouse l4)
    (connected l1 central_warehouse)
    (connected l2 central_warehouse)
    (connected l3 central_warehouse)
    (connected l4 central_warehouse)
  )

  (:goal
    (and
        (served ws1 bolt)
        (served ws1 valve)
        (served ws2 tool)
        (served ws3 bolt)
        (served ws3 valve)
        (served ws4 tool)
        (served ws4 bolt)
    )
  )
)
