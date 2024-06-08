import { Component, OnInit } from '@angular/core';

import Swal from 'sweetalert2'

import { Profile } from './../../models/Profile';
import { Rol } from './../../models/Rol';
import { ActivatedRoute } from '@angular/router';
import { RolService } from 'src/app/services/rol.service';
import { ProfileService } from 'src/app/services/profile.service';

import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-admin-profile-edit',
  templateUrl: './admin-profile-edit.component.html',
  styleUrls: ['./admin-profile-edit.component.scss']
})
export class AdminProfileEditComponent implements OnInit {

  username;

  user: Profile = new Profile();
  profile: Profile = new Profile()
  rols: Array<Rol>;
  newUser: Profile = new Profile();
  selectedRols: Array<Rol> = [];

  // CHECKBOX
  checkboxesDataList: Array<{ id: number, name: string, domain: string, isChecked: boolean }>
  checkedIDs = [];

  constructor(private route: ActivatedRoute,
    private rolService: RolService,
    private profileService: ProfileService,
    private userService: UserService,) { }

  ngOnInit(): void {
    this.username = this.route.snapshot.paramMap.get('usuario');
    this.getProfileAndRols();


  }

  guardar() {
    this.fetchCheckedIDs();

    if (this.validaComboBox() !== true) {
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
      this.newUser.user = this.username;
      this.newUser.roles = this.selectedRols;

      // ELIMINAR PERFILES ANTERIORES
      this.userService.updateUser(this.newUser).toPromise()
        .then(response => {
          // MENSAJE DE CREACION DE USUARIO CORRECTO
          Swal.fire({
            position: 'center',
            icon: 'success',
            title: 'Usuario editado correctamente',
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
  }

  isSelected(rol: Rol): boolean {
    this.profile.roles;//seleccionados del perfil
    let checked = false;

    const index = this.profile.roles.findIndex((srol: Rol) => {
      return srol.id === rol.id;
    });

    if (index >= 0) {
      checked = true;

    }
    return checked;
  }

  // OBTENER PERFILES Y ROLES
  getProfileAndRols() {
    this.profileService.getRawProfileFor(this.username).toPromise()
      .then(response => {
        // OBTENER TODOS LOS ROLES
        this.profile = response;
        console.log(this.profile.roles);

        this.rolService.getAllRols().toPromise()
          .then(response => {
            // CLONANDO EL ARRAY DE ROLES
            this.checkboxesDataList = Object.assign([], response);
            this.rols = response;

            this.checkboxesDataList.forEach((checkbox) => {
              let index = this.profile.roles.findIndex((rol: Rol) => {
                return rol.id === checkbox.id;
              });
              checkbox.isChecked = (index >= 0);
            });

          }, (error) => {
            console.log(error);
            return false;
          })

      }, (error) => {
        console.log(error);
        return false;
      })
  }

  // METODO PARA OBTENER LOS ID SELECCIONADOS EN LOS COMBOBOX
  fetchCheckedIDs() {
    this.checkedIDs = [];
    this.selectedRols = [];

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

  // METODO PARA VALIDAR QUE ESTE SELECCIONADO AL MENOS 1 COMBOBOX
  validaComboBox() {
    let ok = true;
    ok = ok && this.checkedIDs.length != 0;
    return ok;
  }

}
