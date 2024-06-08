import { Component, OnInit } from '@angular/core';
import { Homework } from 'src/app/models/Homework';
import { HomeworkService } from 'src/app/services/homework.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'
import { Profile } from 'src/app/models/Profile';
import { ProfileService } from 'src/app/services/profile.service';
import { Rol } from 'src/app/models/Rol';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss']
})
export class UserDetailComponent implements OnInit {

  homework: Homework = new Homework();
  modoEdicion = false;
  everis = false;
  vacaciones = false;
  casa = false;
  borrarDonde = false;
  profile: Profile = null;
  public domain: string = "COVID19";
  enableEdit: boolean = false;

  constructor(private homeworkService: HomeworkService,
    private route: ActivatedRoute,
    private router: Router,
    private profileService: ProfileService) { }

  ngOnInit(): void {
    this.edit();
    this.loadProfile();
  }

  loadProfile() {
    let user = localStorage.getItem("currentUser");
    if (user == null || user == '') return;
    this.profileService.getFilteredProfileFor(user, this.domain).subscribe(
      (res) => {
        this.profile = <Profile>(res);
        if (res != null) {
          Object.setPrototypeOf(this.profile, Profile.prototype);
          this.profile.setRolesNature();
        }
        else {
          this.profile = new Profile();
          this.profile.user = user;
          this.profile.roles = [];
        }
        //si no hay roles, cargo el 'DEFAULT'
        if (this.profile.roles.length == 0) {
          let rol = new Rol();
          rol.domain = this.domain;
          rol.name = "Default";
          this.profile.roles = [rol];
        }
        console.log(res);

        /* if (this.mustEnableAccess() == false) {
           this.router.navigate(['app']);
         }*/

      },
      (err) => {
        console.log(err);
        this.router.navigate(['//']);
      });
  }

  mustEnableAccess(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if ((this.profile.roles[i].name.toUpperCase() === "EJECUTIVO") ||
        (this.profile.roles[i].name.toUpperCase() === "ADMINISTRADOR"))
        return true;
    }
  }

  edit() {
    const usuario = this.route.snapshot.paramMap.get('usuario');
    const isUsername = this.route.snapshot.queryParams["username"];
    this.modoEdicion = false;
    if (isUsername) {
      this.homeworkService.findByUser(usuario).subscribe(
        (response) => {
          this.homework = response;
          if (this.homework == null) {
            btoa
            this.homework = new Homework();
            this.homework.userName = usuario;
            this.modoEdicion = false;
          }
          else
            this.modoEdicion = true;
          this.donde();

          this.enableEdit = this.allowSave();
        }, (error) => {
          console.log(error);
        }
      );

    }
    else if (usuario != null) {
      this.homeworkService.findById(usuario).subscribe(
        (response) => {
          this.homework = response;
          if (this.homework == null) {
            this.homework = new Homework();
            this.modoEdicion = false;
          }
          else
            this.modoEdicion = true;
          this.donde();
          this.enableEdit = this.allowSave();
        }, (error) => {
          console.log(error);
        }
      );

    }
  }

  guardar() {
    if (this.enableEdit == false) return;
    if (this.valida() === true) {
      if (this.modoEdicion === false) {
        this.homeworkService.guardar(this.homework).subscribe(
          (response) => {
            Swal.fire(
              '¡Guardado!',
              'El registro ha sido guardado con éxito.',
              'success'
            );
            this.router.navigate(['app/edit/' + this.homework.nroEmpleado]);
          }, (error) => {
            console.log(error);
          }
        );
      } else {
        this.homeworkService.updateHomework(this.homework.nroEmpleado, this.homework).subscribe(
          (response) => {
            Swal.fire(
              '¡Actualizado!',
              'El registro ha sido actualizado con éxito.',
              'success'
            );
          }, (error) => {
            console.log(error);
          }
        );
      }
    } else {
      Swal.fire({
        title: '<strong>Campos faltantes</strong>',
        icon: 'info',
        html:
          'Los campos ' +
          '<i>Nombre</i>, ' +
          '<i>N° Empleado</i>, ' +
          '<i>Usuario</i>, ' +
          '<i>Cliente</i> y ' +
          '<i>Donde</i> ' +
          'son de caracter obligatorio.',
        showCloseButton: true,
        focusConfirm: true,
        confirmButtonText: 'Entendido!'
      })
    }
  }

  allowSave(): boolean {
    console.log("allow");
    let user = localStorage.getItem("currentUser");
    if (user == null || user == '') return false;
    if (user === this.homework.userName) return true;
    return false;
  }
  valida() {
    let ok = true;
    ok = ok && this.homework.cliente != null;
    ok = ok && this.homework.cliente !== '';
    ok = ok && this.homework.nroEmpleado != null;
    ok = ok && this.homework.userName != null;
    ok = ok && this.homework.nombre != null;
    ok = ok && this.homework.nombre !== '';
    ok = ok && this.homework.donde != null;
    return ok;
  }

  donde() {
    this.showSection(this.homework.donde);
  }

  showSection(donde: string) {

    this.everis = false;
    this.vacaciones = false;
    this.casa = false;

    if (this.borrarDonde) {
      this.limpiaCamposDonde();
    } else {
      this.borrarDonde = true;
    }

    if ('Everis' === donde) {
      this.everis = true;
    }

    if ('Remoto desde casa' === donde) {
      this.casa = true;
    }

    if ('Cliente' === donde) {
      this.casa = true;
    }

    if ('Vacaciones' === donde) {
      this.vacaciones = true;
    }

    if ('Licencia' === donde) {
      this.vacaciones = true;
    }
  }

  limpiaCamposDonde() {
    this.homework.oficinaEveris = '';
    this.homework.direccion = '';
    this.homework.comuna = '';
    this.homework.region = '';
    this.homework.fechaInicio = null;
    this.homework.fechaFin = null;
  }
}
