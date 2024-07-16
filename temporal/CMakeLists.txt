cmake_minimum_required(VERSION 3.5)
project(temporal)


find_package(ament_cmake REQUIRED)
find_package(rclcpp REQUIRED)
find_package(rclcpp_action REQUIRED)
find_package(plansys2_msgs REQUIRED)
find_package(geometry_msgs REQUIRED)
find_package(nav2_msgs REQUIRED)
find_package(plansys2_domain_expert REQUIRED)
find_package(plansys2_executor REQUIRED)
find_package(plansys2_planner REQUIRED)
find_package(plansys2_problem_expert REQUIRED)
find_package(plansys2_pddl_parser REQUIRED)

set(CMAKE_CXX_STANDARD 17)

set(dependencies
    rclcpp
    rclcpp_action
    plansys2_msgs
    nav2_msgs
    plansys2_domain_expert
    plansys2_executor
    plansys2_planner
    plansys2_problem_expert
    plansys2_pddl_parser
)

add_executable(move_action_node src/move_action_node.cpp)
ament_target_dependencies(move_action_node ${dependencies})

add_executable(move_agent_action_node src/move_agent_action_node.cpp)
ament_target_dependencies(move_agent_action_node ${dependencies})

add_executable(fill_box_action_node src/fill_box_action_node.cpp)
ament_target_dependencies(fill_box_action_node ${dependencies})

add_executable(empty_action_node src/empty_action_node.cpp)
ament_target_dependencies(empty_action_node ${dependencies})

add_executable(pick_action_node src/pick_action_node.cpp)
ament_target_dependencies(pick_action_node ${dependencies})

add_executable(remove_action_node src/remove_action_node.cpp)
ament_target_dependencies(remove_action_node ${dependencies})

install(DIRECTORY launch pddl params DESTINATION share/${PROJECT_NAME})

install(TARGETS
  move_action_node
  move_agent_action_node
  fill_box_action_node
  empty_action_node
  pick_action_node
  remove_action_node
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION lib/${PROJECT_NAME}
)

if(BUILD_TESTING)
  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()

  find_package(ament_cmake_gtest REQUIRED)
endif()

ament_export_dependencies(${dependencies})

ament_package()
