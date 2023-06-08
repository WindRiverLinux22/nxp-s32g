make_nxp_s32g_wic() {
    set -e
    cd ${IMGDEPLOYDIR}
    if echo ${UBOOT_CONFIG} | grep -q s32g; then
        plats="$(echo ${UBOOT_CONFIG} | sed 's/_[^ ]*//g' | tr ' ' '\n' | sort -u)"
    else
        plats="${UBOOT_CONFIG}"
    fi
    for plat in ${plats}; do

        atf_s32="${DEPLOY_DIR_IMAGE}/atf-$plat.s32"
        ofname="${IMAGE_BASENAME}-${MACHINE}-$plat${IMAGE_VERSION_SUFFIX}.rootfs.wic"
        cp ${IMAGE_NAME}.rootfs.wic $ofname
        if [ "${HSE_SEC_ENABLED}" = "1" ]; then
            dd_offset=`cat ${DEPLOY_DIR_IMAGE}/${plat}_dd_offset`

            dd if=$atf_s32 of=$ofname seek=1 skip=1 conv=notrunc,fsync
            dd if=$atf_s32.signature of=$ofname seek=`printf "%d" ${dd_offset}` oflag=seek_bytes conv=notrunc,fsync
        else
            dd if=$atf_s32 of=$ofname conv=notrunc seek=512 skip=512 oflag=seek_bytes iflag=skip_bytes
        fi
        if [ $plat = "aptiv_cvc_fl"  ]; then
            mv -f $ofname ${IMAGE_NAME}.rootfs.wic
            break
        fi
        if [ $plat = "s32g3xxaevb" ]; then
            plat="evb3"
        elif [ $plat = "s32g2xxaevb" ]; then
            plat="evb"
        else
            plat="$(echo $plat | grep -o '....$')"
        fi
        linkname="${IMAGE_LINK_NAME}-$plat.wic"
        ln -sf $ofname $linkname
    done

    if [ $plat != "aptiv_cvc_fl" ]; then
        rm -f ${IMAGE_LINK_NAME}.wic
        rm -f ${IMAGE_NAME}.rootfs.wic
    fi
}

do_image_wic[postfuncs] += "${@bb.utils.contains('MACHINE_FEATURES', 'omit_atf', '', 'make_nxp_s32g_wic', d)}"

BOOTFS_EXTRA_CMD ?= " \
    set -e; \
    if echo ${UBOOT_CONFIG} | grep -q s32g; then \
        plats="$(echo ${UBOOT_CONFIG} | sed 's/_[^ ]*//g' | tr ' ' '\n' | sort -u)"; \
    else \
        plats="${UBOOT_CONFIG}" ;\
    fi; \
    for plat in ${plats}; do \
        atf_s32="${DEPLOY_DIR_IMAGE}/atf-$plat.s32"; \
        if [ $plat = "s32g3xxaevb" ]; then \
            plat_abbr="evb3"; \
        elif [ $plat = "s32g2xxaevb" ]; then \
            plat_abbr="evb"; \
        else \
            plat_abbr="$(echo $plat | grep -o '....$')"; \
        fi; \
        ofname="${plat_abbr}.ustart.img"; \
        cp ustart.img $ofname; \
        if [ "${HSE_SEC_ENABLED}" = "1" ]; then \
            dd_offset=\`cat ${DEPLOY_DIR_IMAGE}/${plat}_dd_offset\`; \
            dd if=$atf_s32 of=$ofname seek=1 skip=1 conv=notrunc,fsync; \
            dd if=$atf_s32.signature of=$ofname seek=\`printf "%d" ${dd_offset}\` oflag=seek_bytes conv=notrunc,fsync; \
        else \
            dd if=$atf_s32 of=$ofname conv=notrunc seek=512 skip=512 oflag=seek_bytes iflag=skip_bytes; \
        fi; \
    done; \
    rm -f ustart.img; \
    if [ $plat = "aptiv_cvc_fl" ]; then \
        mv -f $ofname ustart.img; \
    fi; \
"
