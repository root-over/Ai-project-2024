# generated from ament/cmake/core/templates/nameConfig.cmake.in

# prevent multiple inclusion
if(_temporal_CONFIG_INCLUDED)
  # ensure to keep the found flag the same
  if(NOT DEFINED temporal_FOUND)
    # explicitly set it to FALSE, otherwise CMake will set it to TRUE
    set(temporal_FOUND FALSE)
  elseif(NOT temporal_FOUND)
    # use separate condition to avoid uninitialized variable warning
    set(temporal_FOUND FALSE)
  endif()
  return()
endif()
set(_temporal_CONFIG_INCLUDED TRUE)

# output package information
if(NOT temporal_FIND_QUIETLY)
  message(STATUS "Found temporal: 0.0.4 (${temporal_DIR})")
endif()

# warn when using a deprecated package
if(NOT "" STREQUAL "")
  set(_msg "Package 'temporal' is deprecated")
  # append custom deprecation text if available
  if(NOT "" STREQUAL "TRUE")
    set(_msg "${_msg} ()")
  endif()
  # optionally quiet the deprecation message
  if(NOT ${temporal_DEPRECATED_QUIET})
    message(DEPRECATION "${_msg}")
  endif()
endif()

# flag package as ament-based to distinguish it after being find_package()-ed
set(temporal_FOUND_AMENT_PACKAGE TRUE)

# include all config extra files
set(_extras "ament_cmake_export_dependencies-extras.cmake")
foreach(_extra ${_extras})
  include("${temporal_DIR}/${_extra}")
endforeach()
