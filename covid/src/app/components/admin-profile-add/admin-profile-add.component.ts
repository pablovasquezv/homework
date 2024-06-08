import { Component, OnInit } from '@angular/core';

import Swal from 'sweetalert2'

import { Rol } from './../../models/Rol';
import { Profile } from './../../models/Profile';
import { Router } from '@angular/router';
import { RolService } from 'src/app/services/rol.service';
import { UserService } from 'src/app/services/user.service';


@Component({
  selector: 'app-admin-profile-add',
  templateUrl: './admin-profile-add.component.html',
  styleUrls: ['./admin-profile-add.component.scss']
})
export class AdminProfileAddComponent implements OnInit {
  rol: Rol = null;
  user: Profile = new Profile();
  newUser: Profile = new Profile();
  rols: Array<Rol>;
  users: Array<Profile>;
  selectedRols: Array<Rol> = [];

  // CHECKBOX
  checkedIDs = [];
  checkboxesDataList: Array<{ id: number, name: string, domain: string, isChecked: boolean }>


  constructor(private rolService: RolService,
    private userService: UserService,) { }

  ngOnInit(): void {
    this.getAllRols();

  }

  // METODO PARA GUARDAR AL NUEVO PERFIL
  guardar() {
    this.fetchCheckedIDs();

    if (this.validaTextField() !== true) {
      Swal.fire({
        title: '<strong>Campos faltantes</strong>',
        icon: 'info',
        html:
          'El campo ' +
          '<i>Nombre de Usuario</i>, ' +
          'es de caracter obligatorio.',
        showCloseButton: true,
        focusConfirm: true,
        confirmButtonText: 'Entendido!'
      })
    } else if (this.validaComboBox() !== true) {
      Swal.fire({
        title: '<strong>Campos faltantes</strong>',
        icon: 'info',
        html:
          'Debe seleccionar al menos un Dominios / Permisos',
        showCloseButton: true,
        focusConfirm: true,
        confirmButtonText: 'Entendido!'
      })
    } else {
      // COMPROBANDO EXISTENCIA DEL USUARIO
      this.userService.findByUsername(this.user.user).toPromise()
        .then(response => {
          // SI EL USUARIO YA EXISTE
          if (response != null) {
            Swal.fire({
              title: '<strong>Usuario Existente</strong>',
              icon: 'info',
              html:
                'El usuario ingresado ya existe, seleccione otro nombre de usuario.',
              showCloseButton: true,
              focusConfirm: true,
              confirmButtonText: 'Entendido!'
            })
          } else {
            // AGREGANDO EL USUARIO A LA BASE DE DATOS
            this.newUser.user = this.user.user;
            this.newUser.roles = this.selectedRols;

            this.userService.createUser(this.newUser).toPromise()
              .then(response => {
                // MENSAJE DE CREACION DE USUARIO CORRECTO
                Swal.fire({
                  position: 'center',
                  icon: 'success',
                  title: 'Usuario creado correctamente',
                  showConfirmButton: false,
                  timer: 1500
                })
              }, (error) => {
                Swal.showValidationMessage(
                  `Error al crear el usuario, favor intentar mas tarde`
                );
                return false;
              })
          }

        }, (error) => {
          Swal.showValidationMessage(
            `Error al verificar si el usuario existe, favor intentar mas tarde`
          );
          return false;
        })
    }

  }

  // METODO PARA OBTENER LOS ID SELECCIONADOS EN LOS COMBOBOX
  fetchCheckedIDs() {
    this.checkedIDs = []

    this.checkboxesDataList.forEach((value, index) => {
      if (value.isChecked) {
        this.checkedIDs.push(value);
        let newObj = new Rol();
        newObj.id = value.id;
        newObj.name = value.name;
        newObj.domain = value.domain;
        this.selectedRols.push(newObj);
      }
    });



  }

  // METODO PARA VALIDAD LOS TEXT FIELD
  validaTextField() {
    let ok = true;
    ok = ok && this.user.user != null;
    ok = ok && this.user.user !== '';
    return ok;
  }

  // METODO PARA VALIDAR QUE ESTE SELECCIONADO AL MENOS 1 COMBOBOX
  validaComboBox() {
    let ok = true;
    ok = ok && this.checkedIDs.length != 0;
    return ok;
  }


  // METODO PARA OBTENER LOS ROLES Y PRINTEARLOS EN LO COMBOBOX
  getAllRols() {
    this.rolService.getAllRols().subscribe(
      (res) => {
        // CLONANDO EL ARRAY DE ROLES
        this.checkboxesDataList = Object.assign([], res);

        this.rols = res;
      },
      (err) => {
        console.log(err);
      }
    );
  }

}
