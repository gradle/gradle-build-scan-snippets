#!/usr/bin/env bash

readonly SUMMARY_FMT="%-30s%s"

info() {
  printf "${INFO_COLOR}%s${RESTORE}\n" "$1"
}

infof() {
  local format_string="$1"
  shift
  # the format string is constructed from the caller's input. There is no
  # good way to rewrite this that will not trigger SC2059, so outright
  # disable it here.
  # shellcheck disable=SC2059
  printf "${INFO_COLOR}${format_string}${RESTORE}\n" "$@"
}

summary_row() {
    infof "${SUMMARY_FMT}" "$1" "$2"
}

print_bl() {
  if [[ "$_arg_debug" == "on" ]]; then
    echo "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
  else
    echo
  fi
}

# Strips color codes from Standard in. This function is intended to be used as a filter on another command:
# print_summary | strip_color_codes
strip_color_codes() {
  # shellcheck disable=SC2001  # I could only get this to work with sed
  sed $'s,\x1b\\[[0-9;]*[a-zA-Z],,g'
}

# Overrides the die() function loaded from the argbash-generated parsing libs
die() {
  local _ret="${2:-1}"
  printf "${ERROR_COLOR}%s${RESTORE}\n" "$1"
  echo
  test "${_PRINT_HELP:-no}" = yes && print_help >&2
  exit "${_ret}"
}

print_warnings() {
  local warnings_file="${EXP_DIR}/warnings.txt"
  if [ -f "${warnings_file}" ]; then
    while read -r l; do
      print_bl
      printf "${YELLOW}${BOLD}WARNING: %s${RESTORE}\n" "$l"
    done <"${warnings_file}"
  fi
}

# This function should be overridden by each top-level build validation script
print_summary() {
    print_bl
}

print_experiment_info() {
 local branch commit_id
 branch=$(git_get_branch)
 commit_id=$(git_get_commit_id)

 info "Summary"
 info "-------"
 summary_row "Project:" "${project_name}"
 summary_row "Git repo:" "${git_repo}"
 summary_row "Git branch:" "${branch}"
 summary_row "Git commit id:" "${commit_id}"
 if [ -z "${project_dir}" ]; then
   summary_row "Project dir:" "."
 else
   summary_row "Project dir:" "${project_dir}"
 fi
 if [[ "${BUILD_TOOL}" == "Maven" ]]; then
   summary_row "Maven goals:" "${tasks}"
   summary_row "Maven arguments:" "${extra_args}"
 else
   summary_row "Gradle tasks:" "${tasks}"
   summary_row "Gradle arguments:" "${extra_args}"
 fi
 summary_row "Experiment:" "${EXP_NO} ${EXP_NAME}"
 summary_row "Experiment id:" "${EXP_SCAN_TAG}"
 summary_row "Experiment run id:" "${RUN_ID}"
 summary_row "Experiment artifact dir:" "${EXP_DIR}"
}

create_receipt_file() {
  print_summary | strip_color_codes > "${RECEIPT_FILE}"
  print_bl >> "${RECEIPT_FILE}"
  print_command_to_repeat_experiment | strip_color_codes >> "${RECEIPT_FILE}"
}