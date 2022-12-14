# Add public key to ATF dtb for multiple platforms
fitimage_assemble:append:nxp-s32g() {
	[ "${ATF_SIGN_ENABLE}" = "1" ] || return

	unset i j
	for plat in ${ATF_PLATFORM}; do
		i=$(expr $i + 1);
		for dtb in ${ATF_DTB}; do
			j=$(expr $j + 1)
			if  [ $j -eq $i ]; then
				${UBOOT_MKIMAGE_SIGN} \
					${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
					-f $1 \
					arch/${ARCH}/boot/$2-$plat

				cp -P ${STAGING_DATADIR}/atf-${dtb} ${B}
				add_key_to_atf="-K ${B}/atf-${dtb}"

				${UBOOT_MKIMAGE_SIGN} \
					${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
					-F -k "${UBOOT_SIGN_KEYDIR}" \
					$add_key_to_atf \
					-r arch/${ARCH}/boot/$2-$plat
			fi
		done
		unset j
	done
	unset i
}

do_deploy:append:nxp-s32g() {
	[ "${ATF_SIGN_ENABLE}" = "1" ] || return

	cd ${B}
	for plat in ${ATF_PLATFORM}; do
		install -m 0644 ${KERNEL_OUTPUT_DIR}/fitImage-$plat $deployDir/
	done

	for dtb in ${ATF_DTB}; do
		install -m 0644 ${B}/atf-${dtb} $deployDir/
	done
}

# Emit signature of the fitImage ITS kernel section
fitimage_emit_section_kernel:append:nxp-s32g() {
	if [ "${ATF_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$kernel_sign_keyname" ] ; then
		sed -i '$ d' $1
		cat << EOF >> $1
                        signature-1 {
                                algo = "$kernel_csum,$kernel_sign_algo";
                                key-name-hint = "$kernel_sign_keyname";
                        };
                };
EOF
	fi
}

# Emit signature of the fitImage ITS DTB section
fitimage_emit_section_dtb:append:nxp-s32g() {
	if [ "${ATF_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$dtb_sign_keyname" ] ; then
		sed -i '$ d' $1
		cat << EOF >> $1
                        signature-1 {
                                algo = "$dtb_csum,$dtb_sign_algo";
                                key-name-hint = "$dtb_sign_keyname";
                        };
                };
EOF
	fi
}

# Emit signature of the fitImage ITS u-boot script section
fitimage_emit_section_boot_script:append:nxp-s32g() {
	if [ "${ATF_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$bootscr_sign_keyname" ] ; then
		sed -i '$ d' $1
		cat << EOF >> $1
                        signature-1 {
                                algo = "$bootscr_csum,$bootscr_sign_algo";
                                key-name-hint = "$bootscr_sign_keyname";
                        };
                };
EOF
	fi
}

# Emit signature of the fitImage ITS ramdisk section
fitimage_emit_section_ramdisk:append:nxp-s32g() {
	if [ "${ATF_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$ramdisk_sign_keyname" ] ; then
		sed -i '$ d' $1
		cat << EOF >> $1
                        signature-1 {
                                algo = "$ramdisk_csum,$ramdisk_sign_algo";
                                key-name-hint = "$ramdisk_sign_keyname";
                        };
                };
EOF
	fi
}

# set key name of the fitImage ITS configuration section
fitimage_emit_section_config:prepend:nxp-s32g() {
	if [ "${ATF_SIGN_ENABLE}" = "1" ] ; then
		conf_sign_keyname="${UBOOT_SIGN_KEYNAME}"
	fi
}

python __anonymous () {
    if d.getVar('ATF_SIGN_ENABLE') == "1":
        atf_pn = 'atf-s32g'
        d.appendVarFlag('do_assemble_fitimage', 'depends', ' %s:do_populate_sysroot' % atf_pn)
}
