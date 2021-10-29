#
# Create
or reset Maria DB database hokan_ng_springboot
#

DROP
DATABASE  IF EXISTS hokan_ng_springboot;

CREATE
DATABASE `hokan_ng_springboot` CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL
ON `hokan_ng_springboot`.* TO `hokan_ng`@`localhost` IDENTIFIED BY 'hokan_ng';
GRANT ALL
ON `hokan_ng_springboot`.* TO `hokan_ng`@`%` IDENTIFIED BY 'hokan_ng';

FLUSH
PRIVILEGES;
